package com.moon.moonbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.service.FileService;

@RestController
@RequestMapping("/file")
public class FileController {

   @Autowired
   private FileService fileService;

   // UPLOAD CUSTOMER PHOTO
   @PostMapping(path = { "/upload-pic" }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
         MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<MessageResponse> postPic(
         @RequestPart(value = "file", required = false) MultipartFile pic) {
      return fileService.uploadPic(pic);
   }

}
