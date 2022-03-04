package com.mhp.mobility.hackathon.showcase.s3.downloader.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public final class S3Helper {
   
   private S3Helper() {}
   
   public static String writeBytesToS3(S3Client s3Client, String bucket, String key, byte[] data) {
      try {
         s3Client.putObject(
            PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.fromBytes(data));
         String s3Path = "s3://" + bucket + "/" + key;
         return s3Path;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   
   public static String readFileFromS3(S3Client s3Client, String bucket, String key) {
      return readFileFromS3(s3Client, bucket, key, StandardCharsets.UTF_8);
   }
   
   public static String readFileFromS3(S3Client s3Client, String bucket, String key, Charset encoding) {
      try {
         GetObjectRequest getObjectRequest = GetObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build();
         ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
         byte[] data = responseBytes.asByteArray();
         String contentStr = new String(data, encoding == null ? StandardCharsets.UTF_8 : encoding);
         return contentStr;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
