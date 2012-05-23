package org.jboss.resteasy.spring.beanprocessor;

import org.jboss.resteasy.plugins.spring.EmbeddedContainerBean;

import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfiguration
{
   @Bean
   public EmbeddedContainerBean embeddedContainer()
   {
      return new EmbeddedContainerBean();
   }

   @Bean
   public SpringBeanProcessor springBeanProcessor()
   {
      return new SpringBeanProcessor(embeddedContainer().getDispatcher());
   }

}
