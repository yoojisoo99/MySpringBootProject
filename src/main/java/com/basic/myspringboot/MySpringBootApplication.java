package com.basic.myspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MySpringBootApplication {

	public static void main(String[] args) {

//        SpringApplication.run(MySpringBootApplication.class, args);
		SpringApplication application = new SpringApplication(MySpringBootApplication.class);
		//어플리케이션 타입 설정하기
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);

	}

	@Bean
	public String hello() {
		System.out.println("=====Spring Bean 입니다. ====");
		return "Hello Bean";
	}

}
