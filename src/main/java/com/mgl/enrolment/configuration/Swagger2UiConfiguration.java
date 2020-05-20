package com.mgl.enrolment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class Swagger2UiConfiguration implements WebMvcConfigurer {

    private final static String ENROLMENT_TAG_DESC = "Rest API resource allowing the enrollment " +
            "of new client, verifying details like ID validity, credit score and prior enrolment." +
            "Finally the API allows to generate a printable document that the client can sign, " +
            "and upload the document again";

    @Bean
    public Docket api() {
        //Register the controllers to swagger
        //Also it is configuring the Swagger Docket
//        return new Docket(DocumentationType.SWAGGER_2).select()
//                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
//                .build();
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.mgl.enrolment"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(createInfo())
//                .apiInfo(apiEndPointsInfo())
                .tags(new Tag("Enrolment Controller", ENROLMENT_TAG_DESC));
    }

    private ApiInfo createInfo() {
        return new ApiInfo(
                "Enrolment API",
                "Enrolment API Documentation",
                "1.0",
                "",
                createContact(),
                "no license",
                "",
                Collections.emptyList()
        );
    }

    private Contact createContact() {
        return new Contact(
                "Matyas Gabor",
                "https://github.com/gmatyas9154",
                "gmatyas9154@gmail.com"
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //enabling swagger-ui part for visual documentation
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
