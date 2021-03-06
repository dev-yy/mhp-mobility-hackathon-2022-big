package com.mhp.mobility.hackathon.showcase.s3.downloader;

import static software.amazon.awssdk.regions.Region.EU_WEST_1;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.mhp.mobility.hackathon.showcase.s3.downloader.utils.HttpDownloader;
import com.mhp.mobility.hackathon.showcase.s3.downloader.utils.S3Helper;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class DownloadNutzdateninformationJsonHandler
                  extends AbstractLambdaActionHandler<SQSEvent, Map<String, String>> {
   
   private static final Logger   LOG    = LogManager.getLogger(DownloadNutzdateninformationJsonHandler.class);
   
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
   protected Map<String, String> execute(SQSEvent input, Context context) {
      Map<String, String> response = new HashMap<>();
      for (SQSMessage message : input.getRecords()) {
         JSONObject o = new JSONObject(message.getBody());
         // String filename = (String)json.get("filename");
         // String filetype = (String)json.get("filetype");
         
         String filename = "Nutzdateninformation.json";
         String filetpye = "NUTZDATENINFORMATION";
         String blFileIdentifier = (String)o.get("blFileIdentifier");
         
         String downloadUrl = "https://325khd2o7k.execute-api.eu-west-1.amazonaws.com/prod/mhp/big/api/v1/services/datenverteilung/NUTZDATENINFORMATION/"
                              + blFileIdentifier;
         byte[] content = HttpDownloader.download(downloadUrl);
         String key = "Downloads/" + blFileIdentifier + "/" + filetpye + "/" + filename;
         String s3Path = S3Helper.writeBytesToS3(s3Client, BUCKET, key, content);
         
         String json = S3Helper.readFileFromS3(s3Client, BUCKET, key);
         
         logInfoResp(getLogger(), this, "downloadNutzdateninformationJson", json, 0);
         
         response.put(downloadUrl, s3Path);
      }
      return response;
   }
   
   @Override
   protected Logger getLogger() {
      return LOG;
   }
   
}
