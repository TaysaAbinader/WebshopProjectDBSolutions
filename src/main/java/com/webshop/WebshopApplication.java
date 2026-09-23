package com.webshop;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Webshop Project REST API",
        version = "1.0.0",
        description = "A RESTful API for managing products, categories, customers, addresses, orders, and suppliers."
    ),
    servers = {
        @Server(url = "/", description = "Local server")
    }
)
public class WebshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebshopApplication.class, args);
    }
}
