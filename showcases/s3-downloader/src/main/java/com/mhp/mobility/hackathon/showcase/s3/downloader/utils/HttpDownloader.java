package com.mhp.mobility.hackathon.showcase.s3.downloader.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public final class HttpDownloader {
   
   /** {@code 404 Not Found} (HTTP/1.0 - RFC 1945) */
   public static final int SC_NOT_FOUND = 404;
   /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
   public static final int SC_OK        = 200;
   
   private HttpDownloader() {}
   
   public static byte[] download(String downloadUrl) {
      try (CloseableHttpClient client = HttpClients.createDefault()) {
         HttpGet get = new HttpGet(downloadUrl);
         String apiKey = "L2YhrYWjNYtGaSsHQL4EvQwgUFGcsT3nXyDeX6dFAzStRjQrzXdCDc99b8paZCmz8KYzTKMvAs3KgBtnhE4D8LgGpUt9t8SGf27wmEpqwhUCN5YfzjNR2Y3aBcSehBGt";
         get.addHeader("x-api-key", apiKey);
         HttpResponse response = client.execute(get);
         String message = EntityUtils.toString(response.getEntity());
         return message.getBytes();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
