package org.jboss.resteasy.spring.beanprocessor;

import org.jboss.resteasy.plugins.spring.ResteasyRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ResourceConfiguration
{
   @Bean
   public ResteasyRegistration singletonRegistration()
   {
      return new ResteasyRegistration("/registered/singleton", "singletonCounter");
   }

   @Bean
   public ResteasyRegistration prototypeRegistration()
   {
      return new ResteasyRegistration("/registered/prototype", "prototypeCounter");
   }

   @Bean
   Counter singletonCounter()
   {
      return new Counter();
   }
   
   @Bean
   @Scope("prototype")
   public Counter prototypeCounter()
   {
      return new Counter();
   }
}
