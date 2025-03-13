package com.sensor.data;

public class SensorData {
	
	private String temperature;
	private String humidity;

    public SensorData(String temperature, String humidity) {
    	this.temperature = temperature;
    	this.humidity = humidity;       
    }

    public String getTemperature() {
        return temperature;
    }
    
    public String getHumidity() {
        return humidity;
    }

}
