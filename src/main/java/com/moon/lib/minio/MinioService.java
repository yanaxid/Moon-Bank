package com.moon.lib.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MinioService {

   // public static final Long DEFAULT_EXPIRY = TimeUnit.HOURS.toSeconds(1);
   private static final Long DEFAULT_EXPIRY = TimeUnit.DAYS.toSeconds(7);

   private final MinioClient minio;
   private final MinioProp prop;

   public String getLink(String filename, Long expiry) {
      try {
         return minio.getPresignedObjectUrl(
               GetPresignedObjectUrlArgs.builder()
                     .method(Method.GET)
                     .bucket(prop.getBucketName())
                     .object(filename)
                     .expiry(Math.toIntExact(expiry), TimeUnit.SECONDS)
                     .build());
      } catch (Exception e) {
         log.error("Error getting link: " + e.getLocalizedMessage(), e);
         throw new RuntimeException("Error getting link", e);
      }
   }

   public String getPublicLink(String filename) {
      return this.getLink(filename, DEFAULT_EXPIRY);
   }

   private String sanitizeForFilename(String input) {
      return input.replaceAll("[^a-zA-Z0-9]", "_");
   }

   private String getFileExtension(String filename) {
      int dotIndex = filename.lastIndexOf('.');
      return (dotIndex == -1) ? "" : filename.substring(dotIndex);
   }

   public String uploadFileToMinio(MultipartFile file, String identifier) throws IOException {
      String sanitizedIdentifier = sanitizeForFilename(identifier);
      String timestamp = String.valueOf(System.currentTimeMillis());
      String fileExtension = getFileExtension(file.getOriginalFilename());

      String generatedFilename = String.format(
            "%s_%s%s",
            sanitizedIdentifier,
            timestamp,
            fileExtension);

      try (InputStream inputStream = file.getInputStream()) {
         minio.putObject(
               PutObjectArgs.builder()
                     .bucket(prop.getBucketName())
                     .object(generatedFilename)
                     .stream(inputStream, file.getSize(), -1)
                     .contentType(file.getContentType())
                     .build());
      } catch (Exception e) {
         throw new IOException("Failed to upload file to MinIO", e);
      }

      log.info("File uploaded: {}", generatedFilename);
      return generatedFilename;
   }

   // Method untuk delete file dari Minio jika ada nama file yang redundan
   public void deleteFile(String filename) throws Exception {
      try {
         minio.removeObject(RemoveObjectArgs.builder()
               .bucket(prop.getBucketName())
               .object(filename)
               .build());
         log.info("File {} deleted from MinIO successfully", filename);
      } catch (Exception e) {
         log.error("Failed to delete file {} from MinIO", filename, e);
         throw new Exception("Failed to delete file from MinIO", e);
      }
   }

}
