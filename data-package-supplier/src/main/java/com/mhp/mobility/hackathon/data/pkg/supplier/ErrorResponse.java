package com.mhp.mobility.hackathon.data.pkg.supplier;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ErrorResponse {
   
   public static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S";
   
   private String             timestamp;
   
   private int                status;
   
   private String             message;
   
   private String             stacktrace;
   
   public ErrorResponse(int status, String message, String stacktrace) {
      this.timestamp = format(now(), ISO8601_PATTERN);
      this.status = status;
      this.message = message;
      this.stacktrace = stacktrace;
   }
   
   public ErrorResponse(int status, String message) {
      this.timestamp = format(now(), ISO8601_PATTERN);
      this.status = status;
      this.message = message;
   }
   
   public String getTimestamp() {
      return timestamp;
   }
   
   public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
   }
   
   public int getStatus() {
      return status;
   }
   
   public void setStatus(int status) {
      this.status = status;
   }
   
   public String getMessage() {
      return message;
   }
   
   public void setMessage(String message) {
      this.message = message;
   }
   
   public String getStacktrace() {
      return stacktrace;
   }
   
   public void setStacktrace(String stacktrace) {
      this.stacktrace = stacktrace;
   }
   
   public static final Timestamp newTimestamp() {
      return new Timestamp(System.currentTimeMillis());
   }
   
   public static final Timestamp now() {
      return newTimestamp();
   }
   
   public static String format(Timestamp timestamp, String pattern) {
      if (null != timestamp) {
         return new SimpleDateFormat(pattern).format(timestamp);
      } else {
         return null;
      }
   }
   
}
