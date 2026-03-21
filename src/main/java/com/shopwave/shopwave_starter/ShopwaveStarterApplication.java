//ATE/8400/14
package com.shopwave.shopwave_starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.shopwave")
public class ShopwaveStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopwaveStarterApplication.class, args);
	}

}
