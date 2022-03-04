package com.mhp.mobility.hackathon.showcase.s3.downloader.utils;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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
}
