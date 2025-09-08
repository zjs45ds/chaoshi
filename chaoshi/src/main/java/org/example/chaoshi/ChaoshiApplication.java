package org.example.chaoshi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
@MapperScan("org.example.chaoshi.mapper")
public class ChaoshiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaoshiApplication.class, args);
	}

}
