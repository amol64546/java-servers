package com.json.springservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/files")
public class StreamFileController {

  @GetMapping("/stream")
  public ResponseEntity<StreamingResponseBody> streamFile() {
    File file = new File("src/main/resources/application.properties");

    if (!file.exists()) {
      return ResponseEntity.notFound().build();
    }

    StreamingResponseBody stream = outputStream -> {
      try (FileInputStream fis = new FileInputStream(file)) {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
      }
    };

//    return ResponseEntity.ok()
//        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
//        .contentType(MediaType.APPLICATION_OCTET_STREAM)
//        .contentLength(file.length())
//        .body(stream);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + file.getName())
        .contentType(MediaType.TEXT_PLAIN)
        .body(stream);
  }



  @PostMapping("/proxy-upload")
  public ResponseEntity<String> proxyUpload() throws IOException {
    // 1. Download file from /download
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<byte[]> downloadResponse = restTemplate.exchange(
        "http://localhost:8080/download", // replace with actual host/port
        HttpMethod.GET,
        null,
        byte[].class
    );

    byte[] fileBytes = downloadResponse.getBody();

    // 2. Prepare multipart body
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // Wrap byte[] as Resource so Spring sees it as a file
    ByteArrayResource resource = new ByteArrayResource(fileBytes) {
      @Override
      public String getFilename() {
        return "proxied.txt";
      }
    };

    body.add("file", resource);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // 3. Upload to /upload
    ResponseEntity<String> response = restTemplate.postForEntity(
        "http://localhost:8080/upload",
        requestEntity,
        String.class
    );

    return ResponseEntity.ok("Upload response: " + response.getBody());
  }



}
