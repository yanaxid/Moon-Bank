package com.moon.moonbank.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateRequest {

   private String customerCode;
   private List<Items> items;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class Items {

      private UUID orderId;
      private String itemCode;
      private Integer quantity;
      private Boolean isActive;
      
   }
}
