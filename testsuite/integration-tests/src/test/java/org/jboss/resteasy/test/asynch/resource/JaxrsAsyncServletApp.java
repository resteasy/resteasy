package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsAsyncServletApp extends Application {
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> classes = new HashSet<Class<?>>();

   public JaxrsAsyncServletApp() {
      classes.add(JaxrsAsyncServletResource.class);
      classes.add(JaxrsAsyncServletServiceUnavailableExceptionMapper.class);
      classes.add(JaxrsAsyncServletPrintingErrorHandler.class);
      singletons.add(new JaxrsAsyncServletJaxrsResource());
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
