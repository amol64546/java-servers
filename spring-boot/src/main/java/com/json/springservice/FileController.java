package com.json.springservice;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.FORM_DATA;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {


  @GetMapping("/download")
  public ResponseEntity<Resource> downloadFile() throws IOException {
    // Replace with the path to your file (can be from classpath or file system)
    File file = new File("src/main/resources/application.properties");

    if (!file.exists()) {
      return ResponseEntity.notFound().build();
    }

    // Wrap the file as a resource
    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

//    return ResponseEntity.ok()
//        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
//        .contentType(MediaType.APPLICATION_OCTET_STREAM)
//        .contentLength(file.length())
//        .body(resource);

    return ResponseEntity.ok()
        // This is optional, tells browser to just display it if it can
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + file.getName())
        // Show as plain text
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(file.length())
        .body(resource);
  }

  @PostMapping("/upload")
  public String handleFileUpload(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return "No file uploaded.";
    }

    String fileDetail = String.format(
        "Received file: %s (%d bytes)",
        file.getOriginalFilename(),
        file.getSize()
    );
    System.out.println(fileDetail);

    try {
      // Convert file bytes to String (UTF-8)
      String content = new String(file.getBytes(), StandardCharsets.UTF_8);

      return "Received file: " + file.getOriginalFilename() + "\n\n--- File Content ---\n" + content;

    } catch (IOException e) {
      return "Failed to read file: " + e.getMessage();
    }

  }

  @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public String handleFileUpload(
      @RequestParam MultiValueMap<String, MultipartFile> fileInputs) {

    StringBuilder response = new StringBuilder();

    for (Map.Entry<String, List<MultipartFile>> entry : fileInputs.entrySet()) {
      String fieldName = entry.getKey();
      List<MultipartFile> files = entry.getValue();

      response.append("Field: ").append(fieldName).append("\n");

      for (MultipartFile file : files) {
        response.append("  - File: ").append(file.getOriginalFilename())
            .append(" (").append(file.getSize()).append(" bytes)\n");

        try {
          String content = new String(file.getBytes()); // Assumes UTF-8 or ASCII
          response.append("    Content:\n").append(content).append("\n");
        } catch (IOException e) {
          response.append("    Error reading file: ").append(e.getMessage()).append("\n");
        }
      }
    }

    return response.toString();
  }

  public void uploadFile() {

    File file = new File("src/main/resources/application.properties");

    FileSystemResource fileResource = new FileSystemResource(file);
    HttpHeaders fileHeaders = new HttpHeaders();
    fileHeaders.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE));

    HttpEntity<Resource> fileEntity = new HttpEntity<>(fileResource, fileHeaders);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", fileEntity);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    try {
      JsonNode responseBody = apiCall("url", requestEntity);
      System.out.println(STR."Response: \{responseBody}");
    } catch (RestClientException e) {
      throw new RuntimeException("Failed to upload pipeline", e);
    }
  }

  public void uploadFile(String content) {
    String fileName = STR."file_\{System.currentTimeMillis()}.yaml";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    InputStreamResource inputStreamResource = new InputStreamResource(
        new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    HttpHeaders partHeaders = new HttpHeaders();
    partHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    partHeaders.add(CONTENT_DISPOSITION,
        STR."\{FORM_DATA}; name=\"file\"; filename=\"\{fileName}\"");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", new HttpEntity<>(inputStreamResource, partHeaders));
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    try {
      JsonNode responseBody = apiCall("url", requestEntity);
      System.out.println(STR."Response: \{responseBody}");
    } catch (RestClientResponseException e) {
      throw new RuntimeException("Failed to upload file to content service", e);
    }
  }

  public JsonNode apiCall(String url, Object requestEntity) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> jsonResponse = restTemplate
          .postForEntity(url, requestEntity, String.class);
      return objectMapper.readTree(jsonResponse.getBody());
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse JSON response", e);
    }
  }
}
