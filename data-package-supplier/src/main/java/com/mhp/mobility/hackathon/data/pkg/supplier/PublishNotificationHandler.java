package com.mhp.mobility.hackathon.data.pkg.supplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.mhp.mobility.hackathon.data.pkg.supplier.pojos.NiMessage;

public class PublishNotificationHandler extends AbstractApiGatewayActionHandler {
   
   private static final Logger LOG = LogManager.getLogger(GetNutzdatenInfosHandler.class);
   
   @Override
   protected APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input, Context context) {
      NiMessage niMessage = readValue(input.getBody(), NiMessage.class);
      
      String topic = "7kijucqv-mhp-mobility-hackathon";
      publishToKafka(topic, niMessage);
      Map<String, Object> response = new HashMap<>();
      response.put("topic", topic);
      response.put("message", niMessage);
      response.put("timestamp", format(now(), ISO8601_PATTERN));
      return buildResponse(response);
   }
   
   @Override
   protected Logger getLogger() {
      return LOG;
   }
   
   private void publishToKafka(String topic, NiMessage niMessage) {
      String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
      String jaasCfg = String.format(jaasTemplate, "7kijucqv", "s9tFt4-t4CsnDFE1ILMZK_x1byMUW6PJ");
      String serializer = StringSerializer.class.getName();
      Properties props = new Properties();
      props.put("bootstrap.servers",
         "sulky-01.srvs.cloudkafka.com:9094,sulky-03.srvs.cloudkafka.com:9094,sulky-02.srvs.cloudkafka.com:9094");
      props.put("enable.auto.commit", "true");
      props.put("auto.commit.interval.ms", "1000");
      props.put("auto.offset.reset", "earliest");
      props.put("session.timeout.ms", "30000");
      props.put("key.serializer", serializer);
      props.put("value.serializer", serializer);
      props.put("security.protocol", "SASL_SSL");
      props.put("sasl.mechanism", "SCRAM-SHA-256");
      props.put("sasl.jaas.config", jaasCfg);
      String message = writeValueAsString(niMessage);
      ProducerRecord<String, String> record = new ProducerRecord<>(topic, UUID.randomUUID().toString(), message);
      try (Producer<String, String> producer = new KafkaProducer<>(props)) {
         producer
            .send(record);
      }
   }
}
