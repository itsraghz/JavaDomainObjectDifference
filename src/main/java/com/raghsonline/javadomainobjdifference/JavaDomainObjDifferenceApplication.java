package com.raghsonline.javadomainobjdifference;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.raghsonline"})
public class JavaDomainObjDifferenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaDomainObjDifferenceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
