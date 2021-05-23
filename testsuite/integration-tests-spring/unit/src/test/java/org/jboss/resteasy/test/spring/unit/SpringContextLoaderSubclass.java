package org.jboss.resteasy.test.spring.unit;

import org.jboss.resteasy.plugins.spring.SpringContextLoader;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import jakarta.servlet.ServletContext;

public class SpringContextLoaderSubclass extends SpringContextLoader {

   @Override
   protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext configurableWebApplicationContext) {
      super.customizeContext(servletContext, configurableWebApplicationContext);
   }
}
