package com.ivi.openlr;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEncryptableProperties
@EnableEurekaClient
public class OpenlrApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenlrApplication.class, args);
    }

//    public void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/actuator")
//                .permitAll().anyRequest().authenticated();
//    }

}
