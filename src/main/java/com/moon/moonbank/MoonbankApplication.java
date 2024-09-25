package com.moon.moonbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = { "com.moon.moonbank", "com.moon.lib.minio" })
public class MoonbankApplication {

   public static void main(String[] args) {
      SpringApplication.run(MoonbankApplication.class, args);
   }

}
