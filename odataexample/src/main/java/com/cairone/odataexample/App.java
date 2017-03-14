package com.cairone.odataexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.sdl.odata.service.ODataServiceConfiguration;

@SpringBootApplication
@Import({ ODataServiceConfiguration.class })
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
