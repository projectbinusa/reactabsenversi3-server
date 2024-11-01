package com.example.absensireact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AbsensiReactApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbsensiReactApplication.class, args);
	}

}
