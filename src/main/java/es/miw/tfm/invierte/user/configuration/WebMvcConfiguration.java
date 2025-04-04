package es.miw.tfm.invierte.user.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns(
            "https://*.invierte.io",
            "http://localhost:*"
        )
        .allowedMethods("PATCH", "GET", "POST", "PUT", "DELETE", "OPTIONS")
        .maxAge(3600);
  }

}
