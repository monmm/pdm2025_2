package com.sensor.data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class CtrlData {
	
	@GetMapping
	public SensorData getData() {
		// Datos Dummy random
		SensorData data = new SensorData("XX", "YY");

		return data;
	}

}
