package com.college.scr.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Course Registration API")
                        .version("1.0.0")
                        .description("RESTful Web API demonstrating Hibernate Many-to-Many (@ManyToMany) relational mapping between Student and Course entities via join table student_courses.")
                        .contact(new Contact()
                                .name("College Registrar")
                                .email("registrar@college.edu"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
