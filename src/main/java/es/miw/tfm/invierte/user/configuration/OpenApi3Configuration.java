package es.miw.tfm.invierte.user.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI 3.
 * This class sets up the OpenAPI definition and security schemes
 * for the application, including support for JWT-based bearer authentication
 * and basic authentication.
 *
 * <p>Utilizes the `@OpenAPIDefinition` and `@SecurityScheme` annotations
 * to define the API's security requirements.
 *
 * @see io.swagger.v3.oas.annotations.OpenAPIDefinition
 * @see io.swagger.v3.oas.annotations.security.SecurityScheme
 * @see io.swagger.v3.oas.annotations.enums.SecuritySchemeType
 *
 * @author denilssonmn
 * @author dev_castle
 */
@Configuration
@OpenAPIDefinition
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
    )
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
    )
public class OpenApi3Configuration {
  //Empty
}
