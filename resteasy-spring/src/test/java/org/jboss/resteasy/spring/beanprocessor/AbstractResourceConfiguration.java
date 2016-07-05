package org.jboss.resteasy.spring.beanprocessor;

import org.springframework.context.annotation.Bean;

/**
 * @author Michiel Meeuwissen
 */
public abstract class AbstractResourceConfiguration
{
   @Bean
   Counter superCounter()
   {
      return new Counter();
   }

}
