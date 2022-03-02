package com.mhp.mobility.hackathon.data.pkg.supplier;

import static software.amazon.awssdk.regions.Region.EU_WEST_1;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.mhp.mobility.hackathon.data.pkg.supplier.utils.StringHelper;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class GetBaselineHandler extends AbstractApiGatewayActionHandler {
   
   private static final Logger   LOG    = LogManager.getLogger(GetNutzdatenInfosHandler.class);
   
   private static final String   BUCKET = System.getenv("BUCKET");
   
   // https://aws.amazon.com/de/blogs/developer/tuning-the-aws-java-sdk-2-x-to-reduce-startup-time/?nc1=b_rp
   private static final S3Client s3Client;
   
   static {
      final String region = System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable());
      final Region awsRegion = region != null ? Region.of(region) : EU_WEST_1;
      s3Client = S3Client.builder()
         .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
         .region(awsRegion)
         .httpClientBuilder(UrlConnectionHttpClient.builder())
         .build();
   }
   
   @Override
   protected APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input, Context context) {
      Map<String, String> pathParameters = input.getPathParameters();
      if (pathParameters == null || pathParameters.isEmpty()) {
         throw new IllegalArgumentException("Path must not be null or empty.");
      }
      
      String fileIdentifier = pathParameters.get("fileidentifier");
      if (StringHelper.isNullOrEmpty(fileIdentifier)) {
         throw new IllegalArgumentException("File-Identifier must not be null or empty.");
      }
      
      String key = String.format("services/datenverteilung/%s/BASELINE/BL_%s", fileIdentifier,
         fileIdentifier);
      
      long start = System.currentTimeMillis();
      logInfoReq(getLogger(), this, "readFileFromS3", getArgs(BUCKET, key));
      String json = readFileFromS3(s3Client, BUCKET, key, StandardCharsets.UTF_8);
      logInfoResp(getLogger(), this, "readFileFromS3", json, System.currentTimeMillis() - start);
      
      return buildResponse(json);
   }
   
   @Override
   protected Logger getLogger() {
      return LOG;
   }
   
}
