package com.json.springservice;

import java.io.File;
import java.io.FileInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
