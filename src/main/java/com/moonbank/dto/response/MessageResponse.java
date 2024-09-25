package com.moonbank.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
   private String message;
   private int statusCode;
   private String status;
   private Object data;
   private Meta meta;

   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   @Builder
   public static class Meta {
      private Long total; // Total items in the dataset
      private int perPage; // Items per page
      private int currentPage; // Current page number
      private int lastPage; // Last page number
   }

   public MessageResponse(String message, int statusCode, String status) {
      this.message = message;
      this.statusCode = statusCode;
      this.status = status;
   }

   public MessageResponse(String message, int statusCode, String status, Object data) {
      this.message = message;
      this.statusCode = statusCode;
      this.status = status;
      this.data = data;
   }
}
