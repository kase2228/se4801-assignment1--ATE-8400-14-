//ATE/8400/14
package com.shopwave.shopwave_starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.shopwave")
@EnableJpaRepositories("com.shopwave.repository")
@EntityScan("com.shopwave.model")
public class ShopwaveStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopwaveStarterApplication.class, args);
	}

}
