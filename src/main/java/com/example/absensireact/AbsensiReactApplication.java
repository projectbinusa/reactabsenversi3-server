package com.example.absensireact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.example.absensireact"})
public class AbsensiReactApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbsensiReactApplication.class, args);
	}

}