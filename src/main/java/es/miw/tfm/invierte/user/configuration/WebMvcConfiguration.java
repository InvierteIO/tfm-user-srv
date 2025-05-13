package es.miw.tfm.invierte.user.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Web MVC.
 * This class customizes the Spring MVC configuration, including
 * setting up CORS mappings to allow cross-origin requests.
 *
 * <p>Implements the `WebMvcConfigurer` interface to provide custom configurations.
 *
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 * @see org.springframework.web.servlet.config.annotation.CorsRegistry
 *
 * @author denilssonmn
 * @author dev_castle
 */
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
