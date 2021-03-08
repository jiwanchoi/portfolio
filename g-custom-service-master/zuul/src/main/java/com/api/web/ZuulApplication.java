package com.api.web;

import com.api.web.filter.Gcsfilter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class ZuulApplication extends SpringBootServletInitializer {
	private static final Logger logger = LoggerFactory.getLogger(ZuulApplication.class);

	public static void main(String[] args) {
		logger.debug("###### ZUUL IN #########");
		SpringApplication.run(ZuulApplication.class, args);
		logger.debug("####### ZUUL STRT ##########");
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ZuulApplication.class);
	}

	/*G-Custom서버 인증필터*/
	@Bean
    public Gcsfilter Gcsfilter() {
        return new Gcsfilter();
    }

	@Bean
	public FallbackProvider zuulFallbackProvider() {
		ZuulFallbackProvider routeZuulFallback = new ZuulFallbackProvider();
		routeZuulFallback.setRoute("kthcorp-example");
		return routeZuulFallback;
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
//		return new RestTemplate();
		return new RestTemplate() {{
			setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder
					.create()
					.setConnectionManager(new PoolingHttpClientConnectionManager() {{
						setDefaultMaxPerRoute(200);
						setMaxTotal(1200);
					}})
					.build()) {{
						setConnectTimeout(5000);
						setReadTimeout(10000);
						setConnectionRequestTimeout(5000);
					}});
		}};
	}
}