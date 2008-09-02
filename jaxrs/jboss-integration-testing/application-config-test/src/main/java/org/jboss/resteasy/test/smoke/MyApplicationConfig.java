package org.jboss.resteasy.test.smoke;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MyApplicationConfig extends Application
{
   private Set<Class<?>> classes = new HashSet<Class<?>>();
   private Set<Object> singletons = new HashSet<Object>();

   public MyApplicationConfig()
   {
      classes.add(AppConfig.MyResource.class);
      singletons.add(new AppConfig.QuotedTextWriter());
   }

   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

}
