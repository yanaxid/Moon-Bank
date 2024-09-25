package com.moon.moonbank.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {

   private final MessageSource messageSource;

   @Autowired
   public MessageUtil(MessageSource messageSource) {
      this.messageSource = messageSource;
   }

   public String get(String code, Object... args) {
      Locale locale = LocaleContextHolder.getLocale();
      return getMessage(code, locale, args);
   }

   public String get(Locale locale, String code, Object... args) {
      return getMessage(code, locale, args);
   }

   private String getMessage(String code, Locale locale, Object... args) {
      return messageSource.getMessage(code, args, code, locale);
   }
}
