package com.aimusic.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置类
 * 用于配置Swagger UI的API文档信息
 *
 * @author AI Music Team
 * @version 0.1.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI音乐平台 API")
                        .version("0.1.0")
                        .description("AI音乐平台后端API文档")
                        .termsOfService("http://aimusic.com/terms/")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
} 