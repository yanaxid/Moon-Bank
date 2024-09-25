package com.moon.lib.minio;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
   @Bean
   public MinioClient minioClient(MinioProp props) {
      return MinioClient.builder()
            .endpoint(props.getUrl())
            .credentials(props.getUsername(), props.getPassword())
            .build();

   }
}
