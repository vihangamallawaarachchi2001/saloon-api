package site.vihanga.himaya.saloon_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SaloonApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaloonApiApplication.class, args);
	}


}
