package com.sensor.data;

import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class CtrlData {

  private final Random random = new Random();
	
	@GetMapping
	public SensorData getData() {
    
		// Datos Dummy random
    String temperature = String.format("%.2f", random.nextDouble() * 50); 
    String humidity = String.format("%.2f", random.nextDouble() * 100);  

		return new SensorData(temperature, humidity);

	}

}
