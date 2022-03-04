package com.mhp.mobility.hackathon.showcase.kafka.subscriber;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mhp.mobility.hackathon.showcase.kafka.subscriber.utils.StringHelper;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public abstract class AbstractLambdaActionHandler<ACTION, RESULT> implements RequestHandler<ACTION, RESULT> {
   
   static {
      // https://docs.aws.amazon.com/de_de/sdk-for-java/latest/developer-guide/security-java-tls.html
      System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
   }
   
   protected final static ObjectMapper OBJECTMAPPER = new ObjectMapper();
   
   static {
      OBJECTMAPPER.enable(SerializationFeature.INDENT_OUTPUT);
      OBJECTMAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      OBJECTMAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
      OBJECTMAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      // OBJECTMAPPER.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
      // OBJECTMAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      OBJECTMAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
      OBJECTMAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
   }
   
   public RESULT handleRequest(ACTION input, Context context) {
      try {
         long start = System.currentTimeMillis();
         
         logInfoReq(getLogger(), this, "handleRequest", getArgs(input, context));
         
         RESULT result = execute(input, context);
         
         logInfoResp(getLogger(), this, "handleRequest", result, System.currentTimeMillis() - start);
         return result;
      } catch (Throwable e) {
         logError(getLogger(), this, "handleRequest", getArgs(input, context), e);
         throw new RuntimeException("handleRequest failed", e);
      }
   }
   
   protected void logError(Logger logger, Object initiator,
                           String action,
                           Object[] args, Throwable e, Object... additionalInfos) {
      String entry = getErrorLogEntry(initiator, action, args, e, additionalInfos);
      String exception = buildExceptionEntry(e);
      entry = StringHelper.getWithoutLastElement(entry, "}");
      entry = "{" + entry + ", \"exception\": " + exception + "}";
      logger.error(entry);
   }
   
   private static String buildExceptionEntry(Throwable t) {
      String exception = null;
      Map<String, Object> exceptionMap = new TreeMap<String, Object>();
      exceptionMap.put("cause",
         t != null ? t.getCause() != null ? t.getCause().getClass().getName() : t.getClass().getName() : "");
      exceptionMap.put("message",
         t != null ? !StringHelper.isNullOrEmpty(t.getMessage()) ? jsonEscape(t.getMessage()) : t.getClass().getName()
                   : "");
      String stackTrace = getStackTrace(t);
      exception = stackTrace;
      return exception;
   }
   
   private String getErrorLogEntry(Object initiator, String action, Object[] args, Throwable t,
                                   Object[] additionalInfos) {
      String msg = null;
      LogEntry logMessage = new LogEntry(initiator.getClass().getName(), action, args,
         t != null ? !StringHelper.isNullOrEmpty(t.getMessage()) ? jsonEscape(t.getMessage()) : null : null,
         additionalInfos);
      msg = writeValueAsString(logMessage);
      msg = msg.replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}");
      msg = StringHelper.getWithoutFirstElement(msg, "{");
      msg = StringHelper.getWithoutLastElement(msg, "}");
      return msg;
   }
   
   protected void logInfoResp(Logger logger, Object initiator,
                              String action, Object returnValue, long duration,
                              Object... additionalInfos) {
      String entry = getRespLogEntry(initiator, action, returnValue, duration, additionalInfos);
      entry = String.format("{%s}", entry);
      getLogger().info(entry);
   }
   
   private String getRespLogEntry(Object initiator, String action, Object returnValue, long duration,
                                  Object[] additionalInfos) {
      String msg = null;
      LogEntry logMessage = new LogEntry(
         initiator instanceof String ? (String)initiator : initiator.getClass().getName(), action, returnValue,
         duration,
         additionalInfos);
      msg = writeValueAsString(logMessage);
      msg = msg.replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}");
      msg = StringHelper.getWithoutFirstElement(msg, "{");
      msg = StringHelper.getWithoutLastElement(msg, "}");
      return msg;
   }
   
   protected void logInfoReq(Logger logger, Object initiator,
                             String action, Object[] args, Object... additionalInfos) {
      String entry = getReqLogEntry(initiator, action, args, additionalInfos);
      entry = String.format("{%s}", entry);
      getLogger().info(entry);
   }
   
   private String getReqLogEntry(Object initiator, String action, Object[] args, Object[] additionalInfos) {
      String msg = null;
      LogEntry logMessage = new LogEntry(
         initiator instanceof String ? (String)initiator : initiator.getClass().getName(),
         action, args, additionalInfos);
      msg = writeValueAsString(logMessage);
      msg = msg.replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}");
      msg = StringHelper.getWithoutFirstElement(msg, "{");
      msg = StringHelper.getWithoutLastElement(msg, "}");
      return msg;
   }
   
   protected abstract RESULT execute(ACTION input, Context context);
   
   protected abstract Logger getLogger();
   
   private static String getStackTrace(Throwable throwable) {
      final StringWriter sw = new StringWriter();
      final PrintWriter pw = new PrintWriter(sw, true);
      throwable.printStackTrace(pw);
      String stackTrace = jsonEscape(sw.getBuffer().toString());
      return stackTrace;
   }
   
   private static String jsonEscape(String string) {
      return StringHelper.getStringOrEmptyIfNull(string).replace("\"", "'").replace("\r", "").replace("\t", "");
   }
   
   protected String writeValueAsString(Object obj) {
      try {
         String json;
         json = OBJECTMAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
         return json;
      } catch (IOException e) {
         logError(getLogger(), this, "writeValueAsString", getArgs(obj), e,
            UUID.randomUUID().toString());
         throw new RuntimeException("writeValueAsString failed", e);
      }
   }
   
   protected static Object[] getArgs(Object... args) {
      List<Object> result = new ArrayList<>();
      if (args != null) {
         for (Object arg : args) {
            result.add(arg);
         }
      }
      return result.toArray(new Object[0]);
   }
   
   protected static Object[] noArgs() {
      return null;
   }
   
   protected static Object noReturnValue() {
      return null;
   }
   
   protected String readFileFromS3(S3Client s3Client, String bucket, String key, Charset encoding) {
      long start = System.currentTimeMillis();
      logInfoReq(getLogger(), this, "readFileFromS3", getArgs(bucket, key, encoding));
      try {
         GetObjectRequest getObjectRequest = GetObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build();
         ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
         byte[] data = responseBytes.asByteArray();
         String contentStr = new String(data, encoding == null ? StandardCharsets.UTF_8 : encoding);
         logInfoResp(getLogger(), this, "readFileFromS3", data.length,
            System.currentTimeMillis() - start);
         return contentStr;
      } catch (Exception e) {
         logError(getLogger(), this, "readFileFromS3", getArgs(bucket, key, encoding), e);
         throw new RuntimeException(e);
      }
   }
   
   protected byte[] readBytesFromS3(S3Client s3Client, String bucket, String key) {
      long start = System.currentTimeMillis();
      logInfoReq(getLogger(), this, "readBytesFromS3", getArgs(bucket, key));
      try {
         GetObjectRequest getObjectRequest = GetObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build();
         ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
         byte[] data = responseBytes.asByteArray();
         logInfoResp(getLogger(), this, "readBytesFromS3", data.length,
            System.currentTimeMillis() - start);
         return data;
      } catch (Exception e) {
         logError(getLogger(), this, "readBytesFromS3",
            getArgs(bucket, key),
            e);
         throw new RuntimeException(e);
      }
   }
   
   protected List<String> listObjects(S3Client s3Client, String bucket, String prefix) {
      long start = System.currentTimeMillis();
      logInfoReq(getLogger(), this, "listObjects", getArgs(bucket, prefix));
      
      ListObjectsV2Response result = s3Client.listObjectsV2(ListObjectsV2Request.builder()
         .bucket(bucket)
         .prefix(prefix)
         .build());
      
      List<S3Object> objects = result.contents();
      
      List<String> keys = objects.stream().map(o -> o.key()).collect(Collectors.toList());
      
      logInfoResp(getLogger(), this, "listObjects", keys,
         (System.currentTimeMillis() - start));
      
      return keys;
   }
   
   protected <T> T readValue(String json, Class<T> clazz) {
      try {
         return OBJECTMAPPER.readValue(json, clazz);
      } catch (IOException e) {
         logError(getLogger(), this, "readValue", getArgs(json, clazz), e);
         throw new RuntimeException("readValue failed", e);
      }
   }
   
   /**
    * Returns the current timestamp.
    * 
    * @return the current timestamp
    */
   protected Timestamp now() {
      return newTimestamp();
   }
   
   /**
    * Returns the current timestamp.
    * 
    * @return the current timestamp
    */
   protected Timestamp newTimestamp() {
      return new Timestamp(System.currentTimeMillis());
   }
   
   protected String format(Timestamp timestamp, String pattern) {
      if (null != timestamp) {
         return new SimpleDateFormat(pattern).format(timestamp);
      } else {
         return null;
      }
   }
}
