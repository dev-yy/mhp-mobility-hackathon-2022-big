package com.mhp.mobility.hackathon.showcase.kafka.subscriber;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;

public class KafkaSubscriberHandler extends AbstractLambdaActionHandler<Void, Void> {
   
   private static final Logger LOG           = LogManager.getLogger(KafkaSubscriberHandler.class);
   
   // private final static SqsClient sqsClient;
   //
   // static {
   // final String region = System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable());
   // final Region awsRegion = region != null ? Region.of(region) : EU_WEST_1;
   // sqsClient = SqsClient.builder()
   // .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
   // .region(awsRegion)
   // .httpClientBuilder(UrlConnectionHttpClient.builder())
   // .build();
   // }
   
   private static final String TOPIC         = "7kijucqv-mhp-mobility-hackathon-su-big";
   // Change your group id here:
   // Wincent - mhp-mobility-big-1
   // 4Dieter2Victory - mhp-mobility-big-2
   // 42 Mobility - mhp-mobility-big-3
   // Devstars - mhp-mobility-big-4
   // TrueCloud - mhp-mobility-big-5
   // PAEL - mhp-mobility-big-6
   // ByteMe - mhp-mobility-big-7
   private static final String GROUP_ID      = "mhp-mobility-big-1";
   
   // every 1 Minute
   private static final int    SCHEDULE_RATE = 1;
   
   @SuppressWarnings ("deprecation")
   @Override
   protected Void execute(Void input, Context context) {
      String topic = TOPIC;
      String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
      String jaasCfg = String.format(jaasTemplate, "7kijucqv", "s9tFt4-t4CsnDFE1ILMZK_x1byMUW6PJ");
      
      String deserializer = StringDeserializer.class.getName();
      Properties props = new Properties();
      props.put("bootstrap.servers",
         "sulky-01.srvs.cloudkafka.com:9094,sulky-03.srvs.cloudkafka.com:9094,sulky-02.srvs.cloudkafka.com:9094");
      props.put("group.id", GROUP_ID);
      props.put("enable.auto.commit", "true");
      props.put("auto.commit.interval.ms", "1000");
      props.put("auto.offset.reset", "earliest");
      props.put("session.timeout.ms", "30000");
      props.put("key.deserializer", deserializer);
      props.put("value.deserializer", deserializer);
      props.put("security.protocol", "SASL_SSL");
      props.put("sasl.mechanism", "SCRAM-SHA-256");
      props.put("sasl.jaas.config", jaasCfg);
      
      KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
      try {
         consumer.subscribe(Arrays.asList(topic));
         Timestamp start = now();
         while (notExpired(start)) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
               doSomething(record);
               consumer.commitSync();
            }
         }
      }
      finally {
         if (consumer != null) {
            consumer.unsubscribe();
            consumer.close();
         }
      }
      return null;
   }
   
   protected void doSomething(ConsumerRecord<String, String> record) {
      logInfoResp(getLogger(), this, "receivedKafkaNotification",
         getArgs(record.topic(), record.partition(), record.offset(), record.key(), record.value()), 0);
      // e.g. send message to SQS
      // sqsClient.sendMessage(SendMessageRequest.builder()
      // .queueUrl(queueUrl)
      // .messageBody(message)
      // .messageGroupId(messageGroupId)
      // .messageDeduplicationId(messageDeduplicationId)
      // .build());
   }
   
   private boolean notExpired(Date start) {
      int scheduleRate = SCHEDULE_RATE - 1;
      scheduleRate = scheduleRate > 0 ? scheduleRate : 1;
      return new Date().getTime() - start.getTime() < scheduleRate * 1000 * 60;
      
   }
   
   @Override
   protected Logger getLogger() {
      return LOG;
   }
   
}
