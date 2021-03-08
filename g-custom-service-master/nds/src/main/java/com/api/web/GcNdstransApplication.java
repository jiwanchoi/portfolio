package com.api.web;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEurekaClient
@EnableEncryptableProperties
@SpringBootApplication
public class GcNdstransApplication {

	public static void main(String[] args) {
		SpringApplication.run(GcNdstransApplication.class, args);
//		System.out.println("=========== Server Start ===========");
//		StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
//		pbeEnc.setAlgorithm("PBEWithMD5AndDES");
//		pbeEnc.setPassword("gcustom"); //2번 설정의 암호화 키를 입력
//		
//		String enc = pbeEnc.encrypt("gcustom2020"); //암호화 할 내용
//		System.out.println("enc = " + enc); //암호화 한 내용을 출력
//		
//		//테스트용 복호화
//		String des = pbeEnc.decrypt(enc);
//		System.out.println("des = " + des);
//		System.out.println("=========== Server chk ===========");		
	}
}
