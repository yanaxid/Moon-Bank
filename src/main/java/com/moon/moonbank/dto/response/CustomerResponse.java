package com.moon.moonbank.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

   private String customerCode;
   private String customerName;
   private String customerAddress;
   private String customerPhone;
   private Pic pic;
   private Boolean isActive;
   private LocalDateTime lastOrderDate;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class Pic {
      private String pic;
      private String picUrl;

   }

}
