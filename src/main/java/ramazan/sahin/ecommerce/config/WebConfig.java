package ramazan.sahin.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings( @NonNull CorsRegistry  registry) {
        registry.addMapping("/**")  // tüm endpoint'ler için
                .allowedOrigins("http://localhost:4200") // Angular portu
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
