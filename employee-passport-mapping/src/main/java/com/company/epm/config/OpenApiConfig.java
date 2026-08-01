package com.company.epm.config;

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
                        .title("Employee and Passport Mapping API")
                        .version("1.0.0")
                        .description("RESTful Web API demonstrating Hibernate One-to-One (@OneToOne) relational mapping between Employee and Passport entities, featuring cascade operations, lazy loading, and validation.")
                        .contact(new Contact()
                                .name("HR Systems Admin")
                                .email("hr@company.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
