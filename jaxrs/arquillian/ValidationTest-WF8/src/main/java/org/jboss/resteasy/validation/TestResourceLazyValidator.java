package org.jboss.resteasy.validation;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import javax.validation.ValidatorFactory;
import javax.validation.constraints.AssertTrue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

//import org.jboss.as.ee.beanvalidation.LazyValidatorFactory;
import org.jboss.resteasy.plugins.validation.ValidatorContextResolver;

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
   public boolean testLazyValidator()
   {
      Field field = null;
      try
      {
         field = ValidatorContextResolver.class.getDeclaredField("validatorFactory");
         field.setAccessible(true);
         Object o = field.get(null);
         System.out.println("ValidatorFactory: " + o);
         if (o == null)
         {
            return false;
         }
         if (!(o instanceof WeakReference))
         {
            return false;
         }
         @SuppressWarnings("unchecked")
         WeakReference<ValidatorFactory> ref = WeakReference.class.cast(o);
         ValidatorFactory factory = ref.get();
         System.out.println("real ValidatorFactory: " + factory);
         return factory.getClass().getName().equals("org.jboss.as.ee.beanvalidation.LazyValidatorFactory");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
   }
}
