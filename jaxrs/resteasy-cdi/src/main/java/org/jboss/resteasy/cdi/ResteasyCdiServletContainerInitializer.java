package org.jboss.resteasy.cdi;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @author Stuart Douglas
 */
public class ResteasyCdiServletContainerInitializer implements ServletContainerInitializer {
   @Override
   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
      ctx.addListener(JaxrsCdiLifecycleListener.class);
   }
}
