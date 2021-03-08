package com.ivi.ntg6stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
@EnableEurekaClient
public class IviNtg6StatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IviNtg6StatServiceApplication.class, args);
	}

}
