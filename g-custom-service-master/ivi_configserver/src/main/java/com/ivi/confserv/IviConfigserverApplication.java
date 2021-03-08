package com.ivi.confserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class IviConfigserverApplication {

	public static void main(String[] args) {
		System.out.println("############## Config Sever In #############");
		SpringApplication.run(IviConfigserverApplication.class, args);
		System.out.println("############## Config Sever Run #############");
	}

}
