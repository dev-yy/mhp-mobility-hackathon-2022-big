package com.mhp.tempsensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mhp.tempsensor.config.settings.MqttSettings;
import com.mhp.tempsensor.model.EngineTemperatureSensor;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@Configuration
@EnableConfigurationProperties({
        MqttSettings.class
})
public class MqttApp implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MqttApp.class);

    @Autowired
    private MqttSettings settings;

    private Jackson2JsonObjectMapper mapper = new Jackson2JsonObjectMapper();

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

        IMqttClient publisher = new MqttClient(options.getServerURIs()[0], id);
        publisher.connect(options);

        // Publish
        Flux.interval(Duration.ofSeconds(1))
                .map(tick -> ThreadLocalRandom.current().nextDouble(70.0, 72.0))
                .subscribe(temp -> {
                    LOG.info("Temp: " + temp);

                    String payload = "";
                    try {
                        payload = mapper.toJson(new EngineTemperatureSensor(id, temp));
                        MqttMessage msg = new MqttMessage(payload.getBytes());
                        msg.setQos(0);
                        msg.setRetained(false);

                        publisher.publish(settings.getTopic(), msg);
                    } catch (Exception e) {
                        throw new RuntimeException("Exception occurred building mqtt message", e);
                    }
                });

        Thread.currentThread().join();
    }

}
