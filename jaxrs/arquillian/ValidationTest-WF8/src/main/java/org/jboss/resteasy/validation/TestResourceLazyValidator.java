package org.jboss.resteasy.validation;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import javax.validation.ValidatorFactory;
import javax.validation.constraints.AssertTrue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.plugins.validation.ValidatorContextResolver;
import org.jboss.resteasy.spi.validation.GeneralValidator;
//import org.jboss.resteasy.spi.validation.GeneralValidator;
//import org.jboss.as.ee.beanvalidation.LazyValidatorFactory;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright June 12, 2013
 */
@Path("/")
public class TestResourceLazyValidator
{
   @GET
   @Path("lazy")
   @Produces("text/plain")
   @AssertTrue
   public boolean testLazyValidator(@Context Providers providers)
   {
      ContextResolver<GeneralValidator> resolver = providers.getContextResolver(GeneralValidator.class, MediaType.WILDCARD_TYPE);
      System.out.println("resolver: " + resolver);
      if (resolver == null)
      {
         return false;
      }
      Field field = null;
      try
      {
//         field = ValidatorContextResolver.class.getField("validatorFactory");
         field = resolver.getClass().getField("validatorFactory");
         field.setAccessible(true);
         Object factory = field.get(resolver);
         System.out.println("ValidatorFactory: " + factory);
         if (factory == null)
         {
            return false;
         }
         return factory.getClass().getName().equals("org.jboss.as.ee.beanvalidation.LazyValidatorFactory");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
   }
}
