package org.jboss.resteasy.test.spring.deployment.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This java class defines the beans for the application.
 */
@Configuration
public class JavaConfigBeanConfiguration {
   @Bean
   public JavaConfigService service() {
      return new JavaConfigService();
   }

   @Bean
   public JavaConfigResource resource() {
      return new JavaConfigResource();
   }
}
