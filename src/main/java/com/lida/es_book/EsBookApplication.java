package com.lida.es_book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan //使WebFilter WebServlet等起作用 Druid
@SpringBootApplication
public class EsBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsBookApplication.class, args);
	}
}
