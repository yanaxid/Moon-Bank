package com.moon.moonbank.util;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.moon.moonbank.dto.response.MessageResponse;







@Component
public class ResponseUtil {

   private final MessageUtil messageUtil;

   @Autowired
   public ResponseUtil(MessageUtil messageUtil) {
      this.messageUtil = messageUtil;
   }

   public ResponseEntity<MessageResponse> notFound(String messageKey) {
      String message = messageUtil.get(messageKey);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new MessageResponse(
                  message,
                  HttpStatus.NOT_FOUND.value(),
                  HttpStatus.NOT_FOUND.getReasonPhrase()));
   }

   public ResponseEntity<MessageResponse> okWithData(String messageKey, Object data) {
      String message = messageUtil.get(messageKey);
      return ResponseEntity.status(HttpStatus.OK).body(
            new MessageResponse(
                  message,
                  HttpStatus.OK.value(),
                  HttpStatus.OK.getReasonPhrase(),
                  data));
   }

   public ResponseEntity<MessageResponse> ok(String messageKey) {
      String message = messageUtil.get(messageKey);
      return ResponseEntity.status(HttpStatus.OK).body(
            new MessageResponse(
                  message,
                  HttpStatus.OK.value(),
                  HttpStatus.OK.getReasonPhrase()));
   }

   public ResponseEntity<MessageResponse> okWithDataAndMeta(String messageKey, Object data,
         MessageResponse.Meta meta) {
      String message = messageUtil.get(messageKey);
      return ResponseEntity.status(HttpStatus.OK).body(
            new MessageResponse(
                  message,
                  HttpStatus.OK.value(),
                  HttpStatus.OK.getReasonPhrase(),
                  data,
                  meta));
   }

   public ResponseEntity<MessageResponse> badRequest(String messageKey) {
      String message = messageUtil.get(messageKey);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new MessageResponse(
                  message,
                  HttpStatus.BAD_REQUEST.value(),
                  HttpStatus.BAD_REQUEST.getReasonPhrase(),
                  null));
   }

   public ResponseEntity<MessageResponse> internalServerError(String messageKey) {
      String message = messageUtil.get(messageKey);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new MessageResponse(
                  message,
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                  null));
   }

}

// @Autowired
// private ResponseUtil responseUtil;

// public ResponseEntity<MessageResponse> getUser() {
// return responseUtil.notFound("user.not.found");
// }