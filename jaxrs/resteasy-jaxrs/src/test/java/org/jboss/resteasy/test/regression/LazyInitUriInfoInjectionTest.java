package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * RESTEASY-573
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LazyInitUriInfoInjectionTest extends BaseResourceTest
{
   @Path("/test")
   public static class MyTest
   {
      private UriInfo info;

      @Context
      public void setUriInfo(UriInfo i)
      {
         this.info = i;
         System.out.println(i.getClass().getName());
      }

      @GET
      @Produces("text/plain")
      public String get()
      {
         String val = info.getQueryParameters().getFirst("h");
         if (val == null) val = "";
         return val;
      }


   }

   public static class LazySingletonResource implements ResourceFactory
   {
      private Class clazz;
      private Object obj;

      public LazySingletonResource(Class clazz)
      {
         this.clazz = clazz;
      }

      @Override
      public void registered(ResteasyProviderFactory factory)
      {

      }

      public Object createResource(HttpRequest request, HttpResponse response, ResteasyProviderFactory factory)
      {
         if (obj == null)
         {
            try
            {
               obj = clazz.newInstance();
            }
            catch (InstantiationException e)
            {
               throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
               throw new RuntimeException(e);
            }
            factory.getInjectorFactory().createPropertyInjector(clazz, factory).inject(obj);
         }
         return obj;
      }

      public void unregistered()
      {
      }

      public Class<?> getScannableClass()
      {
         return clazz;
      }

      public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
      {
      }
   }


   @BeforeClass
   public static void setup() throws Exception
   {
      dispatcher.getRegistry().addResourceFactory(new LazySingletonResource(MyTest.class));
   }

   @Test
   public void testDup() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.queryParameter("h", "world");
      String val = request.getTarget(String.class);
      Assert.assertEquals(val, "world");

      request = new ClientRequest(generateURL("/test"));
      val = request.getTarget(String.class);
      Assert.assertEquals(val, "");


   }

}
