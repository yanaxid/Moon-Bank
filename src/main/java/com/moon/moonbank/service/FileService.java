package com.moon.moonbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.moon.lib.minio.MinioService;
import com.moon.moonbank.dto.response.FileResponse;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {

   @Autowired
   private ResponseUtil responseUtil;

   @Autowired
   private MinioService minioService;

   //UPLOAD FILE PIC
   public ResponseEntity<MessageResponse> uploadPic(MultipartFile pic) {

      // validate null
      if (pic == null || pic.isEmpty()) {
         return responseUtil.badRequest("error.null");
      }

      // validate extention
      if (validateImageExtension(pic) != null) {
         return responseUtil.badRequest(validateImageExtension(pic));
      }
      // validate size
      final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB
      if (pic.getSize() > MAX_FILE_SIZE) {
         return responseUtil.notFound("file max 2000kb, your file size is " + pic.getSize() / 1024 + "kb");
      }

      //upload
      try {
         String filename = minioService.uploadFileToMinio(pic);
         FileResponse fileResponse = new FileResponse(filename, minioService.getPublicLink(filename));
         return responseUtil.okWithData("success.upload.file", fileResponse);

      } catch (Exception e) {
         return responseUtil.internalServerError("error.upload.file");
      }

   }


   private String validateImageExtension(MultipartFile pic) {

      //extention allowed
      String[] allowedFotoFile = { ".jpg", ".jpeg", ".png" };

      for (String ext : allowedFotoFile) {
         if (minioService.getFileExtension(pic.getOriginalFilename()).toLowerCase().equals(ext)) {
            return null;
         }
      }

      return "file with " + minioService.getFileExtension(pic.getOriginalFilename()) + " extention is not supported";
   }

}
