package com.example.mind_chain;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.mind_chain.mapper")
public class MindChainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindChainApplication.class, args);
	}

}
