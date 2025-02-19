package team16.spring_project1.global.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@RequiredArgsConstructor
public class WebConfig {
  @Bean
  public StandardServletMultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
  }
}
