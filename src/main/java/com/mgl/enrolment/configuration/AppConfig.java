package com.mgl.enrolment.configuration;

import com.mgl.enrolment.binary.BinaryStore;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${binary.store.type}")
    private String defaultBinaryStore;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    @Bean
    @Primary
    public BinaryStore defaultBinaryStore(ApplicationContext context) {
        return context.getBean(defaultBinaryStore, BinaryStore.class);
    }
}
