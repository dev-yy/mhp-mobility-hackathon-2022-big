package com.mhp.tempsensor.model;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EngineTemperatureSensor {
    
    private String sensorId;
    private double temp;
    private String timestamp;

    public EngineTemperatureSensor() {}

    public EngineTemperatureSensor(String sensorId, double temp) {
        this.sensorId = sensorId;
        this.temp = temp;

        Date date = new Date(System.currentTimeMillis());
        // Conversion
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        this.timestamp = sdf.format(date);
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
        return "EngineTemperatureSensor{" +
                "sensorId='" + sensorId + '\'' +
                ", temp=" + temp +
                ", timestamp='" + getTimestamp() + "'" +
                '}';
    }

}
