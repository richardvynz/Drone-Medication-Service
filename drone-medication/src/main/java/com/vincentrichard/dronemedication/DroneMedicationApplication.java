package com.vincentrichard.dronemedication;

import com.vincentrichard.dronemedication.service.DroneService;
import com.vincentrichard.dronemedication.service.serviceImpl.DroneServiceImpl;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title ="DRONE MEDICATION SERVICE(DRONEMEDIC)",
				description ="Backend application for a springboot Drone Medication Service",
				version="v1.0",
				contact= @Contact(
						name ="Richard Vincent",
						email = "richardvynz@gmail.com"
				),
				license = @License(
						name = "Drone Medication Service, Version 1.0",
						url = "github.com/richardvynz/"
				)
		)
)
public class DroneMedicationApplication {
	public static void main(String[] args) {
		SpringApplication.run(DroneMedicationApplication.class, args);
	}


}
