package org.jboss.resteasy.spring.test.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This java class defines the beans for the application.
 */
@Configuration
public class TestConfig {    
    @Bean
    public JavaConfigService service() {
        return new JavaConfigService();
    }
    @Bean
    public JavaConfigResource resource() {
        return new JavaConfigResource();
    }
}
