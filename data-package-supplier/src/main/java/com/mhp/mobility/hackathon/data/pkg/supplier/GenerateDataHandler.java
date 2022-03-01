package com.mhp.mobility.hackathon.data.pkg.supplier;

import static software.amazon.awssdk.regions.Region.EU_WEST_1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mhp.mobility.hackathon.data.pkg.supplier.pojos.GenerateDataRequest;
import com.mhp.mobility.hackathon.data.pkg.supplier.pojos.Nutzdateninformationen;
import com.mhp.mobility.hackathon.data.pkg.supplier.pojos.ReferenzItem;
import com.mhp.mobility.hackathon.data.pkg.supplier.pojos.ReferenzierteDaten;
import com.mhp.mobility.hackathon.data.pkg.supplier.pojos.Status;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class GenerateDataHandler implements RequestHandler<GenerateDataRequest, Map<String, String>> {
   
   private static final int BASELINE_LENGTH = 300000;
   private static final int ODX_LENGTH      = 200000;
   private static final int PDX_LENGTH      = 500000;
   private static final int LUM_LENGTH      = 1000000;
   
   static {
      // https://docs.aws.amazon.com/de_de/sdk-for-java/latest/developer-guide/security-java-tls.html
      System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
   }
   
   private final static ObjectMapper OBJECTMAPPER = new ObjectMapper();
   
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
   public Map<String, String> handleRequest(GenerateDataRequest input, Context context) {
      
      final Nutzdateninformationen ni = new Nutzdateninformationen();
      final String blFileIdentifer = UUID.randomUUID().toString();
      final String niFileIdentifier = UUID.randomUUID().toString();
      
      ni.setFahrzeugprojektProduktKey(UUID.randomUUID().toString());
      final String baselineName = "BL_" + blFileIdentifer + ".bin";
      ni.setArtefaktTyp(baselineName);
      List<ReferenzItem> referenzItemList = new ArrayList<>();
      ni.setReferenzItems(referenzItemList);
      
      for (int i = 0; i < input.getCntItems(); i++) {
         final ReferenzItem referenzItem = new ReferenzItem();
         referenzItem.setDiagnoseAdresse(UUID.randomUUID().toString());
         referenzItem.setSteuergeraetename(UUID.randomUUID().toString());
         referenzItem.setTeilenummer(UUID.randomUUID().toString());
         referenzItem.setSoftwareVersion(UUID.randomUUID().toString());
         referenzItem.setHardwareVersion(UUID.randomUUID().toString());
         final int id = new Random().nextInt(100);
         final String bezeichnung = UUID.randomUUID().toString();
         final Status status = new Status();
         status.setId(id);
         status.setBezeichnung(bezeichnung);
         referenzItem.setStatus(status);
         List<ReferenzierteDaten> rdList = new ArrayList<>();
         int cntOdx = new Random().nextInt(input.getCntOdx());
         for (int j = 0; j < cntOdx; j++) {
            final ReferenzierteDaten rd = new ReferenzierteDaten();
            final String fileIdentifier = UUID.randomUUID().toString();
            final String fileName = UUID.randomUUID().toString() + ".odx";
            rd.setFileIdentifier(fileIdentifier);
            rd.setFilename(fileName);
            rd.setFiletype("SW-ODX");
            rdList.add(rd);
            
            String key = String.format("services/datenverteilung/%s/NUTZDATEN/%s/%s/%s",
               blFileIdentifer, "SW-ODX", fileIdentifier, fileName);
            final String randomStr = generateRandomString(ODX_LENGTH);
            writeBytesToS3(s3Client, BUCKET, key, randomStr.getBytes());
         }
         
         int cntPdx = new Random().nextInt(input.getCntPdx());
         for (int j = 0; j < cntPdx; j++) {
            final ReferenzierteDaten rd = new ReferenzierteDaten();
            final String fileIdentifier = UUID.randomUUID().toString();
            final String fileName = UUID.randomUUID().toString() + ".pdx";
            rd.setFileIdentifier(fileIdentifier);
            rd.setFilename(fileName);
            rd.setFiletype("SW-PDX");
            rdList.add(rd);
            
            String key = String.format("services/datenverteilung/%s/NUTZDATEN/%s/%s/%s",
               blFileIdentifer, "SW-PDX", fileIdentifier, fileName);
            final String randomStr = generateRandomString(PDX_LENGTH);
            writeBytesToS3(s3Client, BUCKET, key, randomStr.getBytes());
         }
         
         int cntLum = new Random().nextInt(input.getCntLum());
         for (int j = 0; j < cntLum; j++) {
            final ReferenzierteDaten rd = new ReferenzierteDaten();
            final String fileIdentifier = UUID.randomUUID().toString();
            final String fileName = UUID.randomUUID().toString() + ".lum";
            rd.setFileIdentifier(fileIdentifier);
            rd.setFilename(fileName);
            rd.setFiletype("SW-LUM");
            rdList.add(rd);
            
            String key = String.format("services/datenverteilung/%s/NUTZDATEN/%s/%s/%s",
               blFileIdentifer, "SW-LUM", fileIdentifier, fileName);
            final String randomStr = generateRandomString(LUM_LENGTH);
            writeBytesToS3(s3Client, BUCKET, key, randomStr.getBytes());
         }
         
         referenzItem.setReferenzierteDaten(rdList);
         referenzItemList.add(referenzItem);
      }
      
      final String niStr = writeValueAsString(ni);
      
      String key = String.format(
         "services/datenverteilung/%s/NUTZDATENINFORMATION/Nutzdateninformationen_%s.json",
         blFileIdentifer, niFileIdentifier);
      
      String niS3Path = writeFileToS3(s3Client, BUCKET, key, "application/json", niStr);
      
      String keyBL = String.format("services/datenverteilung/%s/BASELINE/BL_%s",
         blFileIdentifer, blFileIdentifer);
      final String randomStr = generateRandomString(BASELINE_LENGTH);
      writeBytesToS3(s3Client, BUCKET, keyBL, randomStr.getBytes());
      
      Map<String, String> response = new HashMap<>();
      response.put("BL-File-Identifier", blFileIdentifer);
      response.put("Nutzdateninformationen", niS3Path);
      return response;
   }
   
   public static String generateRandomString(int targetStringLength) {
      int leftLimit = 48; // numeral '0'
      int rightLimit = 122; // letter 'z'
      Random random = new Random();
      
      String generatedString = random.ints(leftLimit, rightLimit + 1)
         .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
         .limit(targetStringLength)
         .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
         .toString();
      
      return generatedString;
   }
   
   public static void writeBytesToS3(S3Client s3Client, String bucket, String key, byte[] data) {
      try {
         s3Client.putObject(
            PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.fromBytes(data));
         s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.fromBytes(data));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   
   protected String writeValueAsString(Object obj) {
      try {
         String json;
         json = OBJECTMAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
         return json;
      } catch (IOException e) {
         throw new RuntimeException("writeValueAsString failed", e);
      }
   }
   
   public static String writeFileToS3(S3Client s3Client, String bucket, String key,
                                      String contentType, String data) {
      ByteArrayOutputStream out = getByteArrayOutputStream(data);
      String filePath = writeFileToS3(s3Client, bucket, key, contentType, out);
      return filePath;
   }
   
   private static ByteArrayOutputStream getByteArrayOutputStream(String data) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
      try {
         out.write(data.getBytes());
         byteArrayOutputStream.flush();
         byteArrayOutputStream.close();
      } catch (Exception e) {
         throw new RuntimeException("getByteArrayOutputStream failed", e);
      }
      return byteArrayOutputStream;
   }
   
   public static String writeFileToS3(S3Client s3Client, String bucket, String key,
                                      String contentType, ByteArrayOutputStream out) {
      // Prepare an InputStream from the ByteArrayOutputStream
      InputStream fis = new ByteArrayInputStream(out.toByteArray());
      // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/examples-s3-objects.html
      // Put file into S3
      s3Client.putObject(
         PutObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build(),
         RequestBody.fromContentProvider(new ContentStreamProvider() {
            @Override
            public InputStream newStream() {
               return fis;
            }
         }, out.toByteArray().length, contentType));
      
      String filePath = "s3://" + bucket + "/" + key;
      
      return filePath;
   }
}
