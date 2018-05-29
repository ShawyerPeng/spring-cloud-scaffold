package com.bitswild;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaClient
@RestController
@SpringBootApplication
public class EurekaProvider2Application {
	@Value("${server.port}")
	String port;

	@RequestMapping("/home")
	public String home() {
		return "Hello world, port:" + port;
	}

	@RequestMapping("/home2")
	public String home2() {
		return "Hello world 2, port:" + port;
	}

	public static void main(String[] args) {
		SpringApplication.run(EurekaProvider2Application.class, args);
	}
}