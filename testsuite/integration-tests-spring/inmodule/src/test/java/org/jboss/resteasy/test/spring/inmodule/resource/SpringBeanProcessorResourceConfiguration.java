package org.jboss.resteasy.test.spring.inmodule.resource;

import org.jboss.resteasy.plugins.spring.ResteasyRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SpringBeanProcessorResourceConfiguration {
    @Bean
    public ResteasyRegistration singletonRegistration() {
        return new ResteasyRegistration("/registered/singleton", "singletonCounter");
    }

    @Bean
    public ResteasyRegistration prototypeRegistration() {
        return new ResteasyRegistration("/registered/prototype", "prototypeCounter");
    }

    @Bean
    SpringBeanProcessorCounter singletonCounter() {
        return new SpringBeanProcessorCounter();
    }

    @Bean
    @Scope("prototype")
    public SpringBeanProcessorCounter prototypeCounter() {
        return new SpringBeanProcessorCounter();
    }
}
