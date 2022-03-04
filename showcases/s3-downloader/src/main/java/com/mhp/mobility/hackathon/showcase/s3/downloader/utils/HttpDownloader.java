package com.mhp.mobility.hackathon.showcase.s3.downloader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class HttpDownloader {
   
   /** {@code 404 Not Found} (HTTP/1.0 - RFC 1945) */
   public static final int SC_NOT_FOUND = 404;
   /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
   public static final int SC_OK        = 200;
   
   private HttpDownloader() {}
   
   public static byte[] download(String downloadUrl) {
      try {
         disableSSLHandshake();
         URL url = new URL(downloadUrl);
         HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
         // conn.setRequestProperty("x-api-key",
         // "L2YhrYWjNYtGaSsHQL4EvQwgUFGcsT3nXyDeX6dFAzStRjQrzXdCDc99b8paZCmz8KYzTKMvAs3KgBtnhE4D8LgGpUt9t8SGf27wmEpqwhUCN5YfzjNR2Y3aBcSehBGt");
         // conn.setRequestProperty("Content-Type", "application/json");
         conn.setRequestMethod("GET");
         
         byte[] binary = readAllBytes(conn.getInputStream());
         
         int statusCode = conn.getResponseCode();
         
         if (SC_NOT_FOUND == statusCode) {
            throw new RuntimeException("Resource not found at" + downloadUrl);
         }
         
         if (SC_OK != statusCode) {
            throw new RuntimeException("Download failed: " + statusCode);
         }
         return binary;
      } catch (Exception e) {
         throw new RuntimeException("download Failed", e);
      }
   }
   
   public static byte[] readAllBytes(InputStream is) throws IOException {
      try {
         byte[] targetArray = new byte[is.available()];
         is.read(targetArray);
         return targetArray;
      }
      finally {
         is.close();
      }
   }
   
   protected static void disableSSLHandshake() throws NoSuchAlgorithmException, KeyManagementException {
      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, createTrustManager(), new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
         public boolean verify(String hostname, SSLSession session) {
            return true;
         }
      };
      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
   }
   
   protected static TrustManager[] createTrustManager() {
      TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
         public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
         }
         
         public void checkClientTrusted(X509Certificate[] certs, String authType) {}
         
         public void checkServerTrusted(X509Certificate[] certs, String authType) {}
      }
      };
      return trustAllCerts;
   }
}
