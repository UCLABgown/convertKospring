package team16.spring_project1.global.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod") // prod 환경일시 비활성화
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                            .info(
                                    new Info()
                                    .title("team16 Spring Project API")
                                    .version("v1.0.0")
                                    .description("Spring Project API 문서")
                            );
    }

}
