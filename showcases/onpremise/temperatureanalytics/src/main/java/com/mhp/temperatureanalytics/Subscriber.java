package com.mhp.temperatureanalytics;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhp.temperatureanalytics.config.MqttSettings;
import com.mhp.temperatureanalytics.dao.TemperatureCrudRepository;
import com.mhp.temperatureanalytics.model.EngineTemperatureSensor;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Configuration
@EnableConfigurationProperties({
        MqttSettings.class
})
public class Subscriber implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(Subscriber.class);

    @Autowired
    private MqttSettings settings;

    @Autowired
    private TemperatureCrudRepository repository;

    private ObjectMapper mapper = new ObjectMapper();


    private String mqttClientId() {
        return "temp-" + UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public void run(String... args) throws Exception {

        String id = mqttClientId();
        LOG.info("Starting Sensor: {}", id);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(
                new String[] { String.format("tcp://%s:%s", settings.getHostname(), settings.getPort()) });
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        IMqttClient subscriber = new MqttClient(options.getServerURIs()[0], id);
        subscriber.connect(options);
        
        subscriber.subscribe(settings.getTopic(), (topic, msg) -> {
            byte[] payload = msg.getPayload();
            LOG.info("[I82] Message received: topic={}, payload={}", topic, new String(payload));            
            EngineTemperatureSensor temp = mapper.readerFor(EngineTemperatureSensor.class).readValue(new String(payload));
            
            Mono<EngineTemperatureSensor> myEvent = repository.save(temp);
            myEvent.subscribe();
            LOG.info("Event Saved: {}", myEvent);            

        });
    }

}
