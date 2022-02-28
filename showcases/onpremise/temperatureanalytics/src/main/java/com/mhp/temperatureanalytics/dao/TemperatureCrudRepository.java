package com.mhp.temperatureanalytics.dao;

import com.mhp.temperatureanalytics.model.EngineTemperatureSensor;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TemperatureCrudRepository extends ReactiveMongoRepository<EngineTemperatureSensor, String> {

}