package com.api.auth.config;

import javax.sql.DataSource;

import org.apache.catalina.realm.DataSourceRealm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import lombok.AllArgsConstructor;

@Configuration
@EnableAuthorizationServer
@AllArgsConstructor
public class AuthConfig extends AuthorizationServerConfigurerAdapter{

    @Autowired
	private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    /**
     * "/oauth/authorize" 요청을 처리하기위한 사용자 저장소 정의.
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {        
        endpoints
                .approvalStore(approvalStore())
                .tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
        ;        
    }

    /**
     * access_token, refresh_token을 저장하는 토큰 저장소에 대한 모든 CRUD를 구현
     * 구현체 : InMemoryTokenStore, JdbcTokenStore, RedisTokenStore
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}	
