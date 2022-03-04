package com.mhp.mobility.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Bean
//    public ApplicationRunner runner(Producer producer) {
//        return (args) -> {
//            for(int i = 1; i < 20; i++) {
//                producer.send(new Message(i, "A simple test message"));
//            }
//        };
//    }

}