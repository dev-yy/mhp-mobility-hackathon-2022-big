package com.mhp.temperatureanalytics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "enginetemperature")
public class EngineTemperatureSensor {

    private String sensorId;
    private double temp;
    @Id
    private String timestamp;

    public EngineTemperatureSensor() {}

    public EngineTemperatureSensor(String sensorId, double temp) {
        this.sensorId = sensorId;
        this.temp = temp;
    }

    public EngineTemperatureSensor(String sensorId, double temp, String timestamp) {
        this.sensorId = sensorId;
        this.temp = temp;
        this.timestamp = timestamp;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{" +
            " sensorId='" + getSensorId() + "'" +
            ", temp='" + getTemp() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            "}";
    }    

}
