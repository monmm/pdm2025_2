package com.sensor.data;

public class SensorData {

	private String humidity;
    private String temperature;

    public SensorData(String humidity, String temperature) {
        this.humidity = humidity;
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemperature() {
        return temperature;
    }
}
