package com.mhp.mobility.hackathon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mhp.mobility.hackathon.pojos.Nutzdateninformationen;
import com.mhp.mobility.hackathon.pojos.ReferenzItem;
import com.mhp.mobility.hackathon.pojos.ReferenzierteDaten;
import com.mhp.mobility.hackathon.pojos.Status;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Datagenerator {
   public static void main(String[] args) throws IOException, URISyntaxException {
      Path newFolder = Paths.get("/testdata/");
      final String testDataPath = newFolder.toAbsolutePath().toString();
      Files.createDirectories(newFolder);
      
      final String blName = UUID.randomUUID().toString();
      final Path blPath = Paths.get(testDataPath + "/BL_" + blName);
      if (!Files.exists(blPath)) {
         Files.createFile(blPath);
         Files.write(blPath, generateRandomString().getBytes());
      }
      
      final Path odxDir = Paths.get(testDataPath + "/SW-ODX/");
      if (!Files.exists(odxDir)) {
         Files.createDirectories(odxDir);
      }
      
      final Path pdxDir = Paths.get(testDataPath + "/SW-PDX/");
      if (!Files.exists(pdxDir)) {
         Files.createDirectories(pdxDir);
      }
      
      final Path lumDir = Paths.get(testDataPath + "/SW-LUM/");
      if (!Files.exists(lumDir)) {
         Files.createDirectories(lumDir);
      }
      
      final Path nin = Paths.get(testDataPath + "/Nutzdateninformationen/");
      if (!Files.exists(nin)) {
         Files.createDirectories(nin);
      }
      
      final Nutzdateninformationen ni = new Nutzdateninformationen();
      ni.setFahrzeugprojektProduktKey(UUID.randomUUID().toString());
      final String baselineName = "BL_" + blName;
      ni.setArtefaktTyp(baselineName);
      List<ReferenzItem> referenzItemList = new ArrayList<>();
      ni.setReferenzItems(referenzItemList);
      
      for (int i = 0; i < 10; i++) {
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
         if (i % 3 == 0) {
            for (int j = 0; j < 3; j++) {
               final ReferenzierteDaten rd = new ReferenzierteDaten();
               final String fileIdentifier = UUID.randomUUID().toString();
               final String fileName = UUID.randomUUID().toString() + ".odx";
               rd.setFileIdentifier(fileIdentifier);
               rd.setFilename(fileName);
               rd.setFiletype("SW-ODX");
               
               final StringBuilder builder = new StringBuilder()
                  .append(testDataPath)
                  .append("/SW-ODX/")
                  .append(fileIdentifier);
               Files.createDirectories(Paths.get(builder.toString()));
               
               final String filePath = new StringBuilder()
                  .append(testDataPath)
                  .append("/SW-ODX/")
                  .append(fileIdentifier)
                  .append("/")
                  .append(fileName)
                  .append(".odx")
                  .toString();
               Files.createFile(Paths.get(filePath));
               final Path path = Paths.get(filePath);
               final String randomStr = generateRandomString();
               Files.write(path, randomStr.getBytes());
               rdList.add(rd);
            }
         } else if (i % 3 == 1) {
            final ReferenzierteDaten rd = new ReferenzierteDaten();
            final String fileIdentifier = UUID.randomUUID().toString();
            final String fileName = UUID.randomUUID().toString() + ".pdx";
            rd.setFileIdentifier(fileIdentifier);
            rd.setFilename(fileName);
            rd.setFiletype("SW-PDX");
            final StringBuilder builder = new StringBuilder()
               .append(testDataPath)
               .append("/SW-PDX/")
               .append(fileIdentifier);
            Files.createDirectories(Paths.get(builder.toString()));
            
            final String filePath = new StringBuilder()
               .append(testDataPath)
               .append("/SW-PDX/")
               .append(fileIdentifier)
               .append("/")
               .append(fileName)
               .append(".pdx")
               .toString();
            Files.createFile(Paths.get(filePath));
            final Path path = Paths.get(filePath);
            final String randomStr = generateRandomString();
            Files.write(path, randomStr.getBytes());
            rdList.add(rd);
         } else {
            final ReferenzierteDaten rd = new ReferenzierteDaten();
            final String fileIdentifier = UUID.randomUUID().toString();
            final String fileName = UUID.randomUUID().toString() + ".lum";
            rd.setFileIdentifier(fileIdentifier);
            rd.setFilename(fileName);
            rd.setFiletype("SW-LUM");
            final StringBuilder builder = new StringBuilder()
               .append(testDataPath)
               .append("/SW-LUM/")
               .append(fileIdentifier);
            Files.createDirectories(Paths.get(builder.toString()));
            
            final String filePath = new StringBuilder()
               .append(testDataPath)
               .append("/SW-LUM/")
               .append(fileIdentifier)
               .append("/")
               .append(fileName)
               .append(".lum")
               .toString();
            Files.createFile(Paths.get(filePath));
            final Path path = Paths.get(filePath);
            final String randomStr = generateRandomString();
            Files.write(path, randomStr.getBytes());
            rdList.add(rd);
         }
         referenzItem.setReferenzierteDaten(rdList);
         referenzItemList.add(referenzItem);
      }
      
      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
      final String niStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ni);
      final String ndPath = new StringBuilder()
         .append(testDataPath)
         .append("/Nutzdateninformationen/")
         .append("Nutzdateninformationen_BL_" + UUID.randomUUID().toString() + ".json")
         .toString();
      Files.createFile(Paths.get(ndPath));
      Files.write(Paths.get(ndPath), niStr.getBytes());
      
   }
   
   public static String generateRandomString() {
      int leftLimit = 48; // numeral '0'
      int rightLimit = 122; // letter 'z'
      int targetStringLength = 2000;
      Random random = new Random();
      
      String generatedString = random.ints(leftLimit, rightLimit + 1)
         .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
         .limit(targetStringLength)
         .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
         .toString();
      
      return generatedString;
   }
}
