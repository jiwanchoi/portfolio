package com.ivi.ntg6.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration //Configuration 어노테이션은 root-context안에 들어갈 코드를 어노테이션으로 만든것이라고 생각하면 된다.
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.uri:}")
    private String uri;

    @Value("${spring.data.mongodb.username:gcustom}")     //application.properties에서 정의한 MongoDB에 계정 아이디
    private String userName;
    
    
    @Value("${spring.data.mongodb.password:ENC(jPjXkHbniKIjBx/G8AZDqFp5FgxHucwJ)}")    //application.properties에서 정의한 MongoDB에 계정 비밀번호
    private String password;
    
    
    @Value("${spring.data.mongodb.database:gcustom}") //application.properties에서 정의한 MongoDB에있는 데이터베이스
    private String database;
 
    @Value("${spring.data.mongodb.address:localhost}") //application.properties에서 정의한 MongoDB에있는 url
    private String address;

    @Value("#{'${spring.data.mongodb.address:localhost}'.split(',')}")
    private List<String> hosts;
    
    @Override
    protected String getDatabaseName() {
        if (uri != null && uri.length() > 0) {
            MongoClientURI mongoClientURI = new MongoClientURI(uri);
            database = mongoClientURI.getDatabase();
        }
        return database;
    }
    
    //레거시 프로젝트에서 xml파일 안에 <bean>태그 안에 있던 코드를 적어놓은것.
    //mongotemplate을 만들어서 리턴해준다.
    public @Bean MongoTemplate mongoTemplate() throws Exception {
        if (uri != null && uri.length() > 0) {
            return new MongoTemplate(mongoDbFactory());
        }
        return new MongoTemplate((MongoClient) mongoClient(), database);
    }
 
//    @Override
//    public MongoClient mongoClient() {
//        MongoCredential credential =  //인증 정보
//                MongoCredential.createCredential(
//                        userName, database, password.toCharArray()); //아이디, 데이터베이스, 비밀번호
//        return new MongoClient(new ServerAddress(address, 27017),
//                Arrays.asList(credential));
//    }

    @Override
    public MongoClient mongoClient() {
        if (uri != null && uri.length() > 0) {
            System.out.println("URI :: " + uri);
            // "mongodb://localhost:37017,localhost:37018/gcustom?replicaSet=gcstm"
            MongoClientURI mongoClientURI = new MongoClientURI(uri);
            System.out.println("mongodb uri : ====> " + uri);
            return new MongoClient(mongoClientURI);
        }

        MongoCredential credential =  //인증 정보
                MongoCredential.createCredential(
                        userName, getDatabaseName(), password.toCharArray()); //아이디, 데이터베이스, 비밀번호
        final List<ServerAddress> serverAddressList = new ArrayList<>();
        for (final String addressString : hosts) {
            String host = "";
            int port = 0;
            String [] splitAddress = addressString.split(":");
            if (splitAddress.length > 1) {
                host = splitAddress[0];
                port = Integer.parseInt(splitAddress[1]);
            } else {
                host = addressString;
                port = 27017; // set default mongodb port
            }
            serverAddressList.add(new ServerAddress(host, port));
        }
        return new MongoClient(serverAddressList, Collections.singletonList(credential));
    }
}