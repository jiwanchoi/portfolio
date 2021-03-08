package com.api.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEurekaClient
@EnableEncryptableProperties
@SpringBootApplication
public class GcAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(GcAuthApplication.class, args);
	}

}
