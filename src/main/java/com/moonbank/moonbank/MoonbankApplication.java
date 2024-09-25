package com.moonbank.moonbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MoonbankApplication {

   public static void main(String[] args) {
      SpringApplication.run(MoonbankApplication.class, args);
   }

}
