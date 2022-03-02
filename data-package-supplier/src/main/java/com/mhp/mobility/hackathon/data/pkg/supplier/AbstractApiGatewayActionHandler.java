package com.mhp.mobility.hackathon.data.pkg.supplier;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mhp.mobility.hackathon.data.pkg.supplier.utils.StringHelper;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public abstract class AbstractApiGatewayActionHandler
                  implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
   
   /**
    * {@code 500 Internal Server Error}.
    * 
    * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.1">HTTP/1.1: Semantics and Content, section
    *      6.6.1</a>
    */
   public static final int    INITERNAL_SERVER_ERROR           = 500;
   
   /** The HTTP {@code X-Powered-By} header field name. */
   public static final String X_POWERED_BY                     = "X-Powered-By";
   /** The HTTP {@code Content-Type} header field name. */
   public static final String CONTENT_TYPE                     = "Content-Type";
   /** The HTTP {@code Access-Control-Allow-Origin} header field name. */
   public static final String ACCESS_CONTROL_ALLOW_ORIGIN      = "Access-Control-Allow-Origin";
   /** The HTTP {@code Access-Control-Allow-Credentials} header field name. */
   public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
   /**
    * Public constant media type for {@code application/json}.
    * 
    * @see #APPLICATION_JSON_UTF8
    */
   public final static String APPLICATION_JSON_VALUE           = "application/json";
   
   public static final String ISO8601_PATTERN                  = "yyyy-MM-dd'T'HH:mm:ss.S";
   
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
   
   public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
      try {
         long start = System.currentTimeMillis();
         
         logInfoReq(getLogger(), this, "handleRequest", getArgs(input, context));
         
         APIGatewayProxyResponseEvent result = execute(input, context);
         
         logInfoResp(getLogger(), this, "handleRequest", result, System.currentTimeMillis() - start);
         return result;
      } catch (Throwable e) {
         Throwable rootCause = findRootCause(e);
         String msg = !StringHelper.isNullOrEmpty(rootCause.getMessage()) ? rootCause.getMessage()
                                                                          : e.getMessage();
         
         logError(getLogger(), this, "handleRequest", getArgs(input, context), e,
            UUID.randomUUID().toString(), msg);
         
         String stacktrace = getStackTrace(e);
         
         ErrorResponse error = new ErrorResponse(INITERNAL_SERVER_ERROR, msg,
            stacktrace);
         
         APIGatewayProxyResponseEvent response = buildResponse(error);
         
         return response;
      }
   }
   
   protected APIGatewayProxyResponseEvent buildResponse(Object result) {
      Map<String, String> headers = new HashMap<>();
      headers.put(X_POWERED_BY, "AWS Lambda & Serverless");
      headers.put(CONTENT_TYPE, APPLICATION_JSON_VALUE);
      headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      headers.put(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      
      APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
      response.setStatusCode(INITERNAL_SERVER_ERROR);
      response.setBody(result instanceof String ? (String)result : writeValueAsString(result));
      response.setHeaders(headers);
      response.setIsBase64Encoded(false);
      return response;
   }
   
   protected APIGatewayProxyResponseEvent buildBinaryBodyResponse(byte[] binaryBody) {
      Map<String, String> headers = new HashMap<>();
      headers.put(X_POWERED_BY, "AWS Lambda & Serverless");
      headers.put(CONTENT_TYPE, APPLICATION_JSON_VALUE);
      headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      headers.put(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      
      APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
      response.setStatusCode(INITERNAL_SERVER_ERROR);
      response.setBody(new String(Base64.getEncoder().encode(binaryBody), StandardCharsets.UTF_8));
      response.setHeaders(headers);
      response.setIsBase64Encoded(true);
      return response;
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
   
   protected abstract APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input, Context context);
   
   protected abstract Logger getLogger();
   
   private static Throwable findRootCause(Throwable throwable) {
      Throwable rootCause = throwable;
      while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
         rootCause = rootCause.getCause();
      }
      return rootCause;
   }
   
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
