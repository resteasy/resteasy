package org.jboss.resteasy.test.nextgen.finegrain.resource;

import junit.framework.Assert;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProgammaticTest extends BaseResourceTest
{
   public static class MyResource
   {
      private UriInfo uriInfo;

      public int counter;

      private HttpHeaders headers;

      private Configurable configurable;

      public void setHeaders(HttpHeaders headers)
      {
         this.headers = headers;
      }

      public MyResource()
      {
      }

      public MyResource(Configurable configurable)
      {
         this.configurable = configurable;
      }

      public String get(String param)
      {
         Assert.assertEquals("hello", param);
         uriInfo.getBaseUri();
         headers.getCookies();
         counter++;
         return "hello";
      }

      public void put(String value)
      {
         Assert.assertEquals("hello", value);
         configurable.getConfiguration();
      }
   }

   @Test
   public void testPerRequest() throws Exception
   {
      Method get = MyResource.class.getMethod("get", String.class);
      Method put = MyResource.class.getMethod("put", String.class);
      Method setter = MyResource.class.getMethod("setHeaders", HttpHeaders.class);
      Field uriInfo = MyResource.class.getDeclaredField("uriInfo");
      Constructor constructor = MyResource.class.getConstructor(Configurable.class);

      ResourceClass resourceclass = ResourceBuilder.rootResource(MyResource.class)
              .constructor(constructor).param(0).context().buildConstructor()
              .method(get).get().path("test").produces("text/plain").param(0).queryParam("a").buildMethod()
              .method(put).put().path("test").consumes("text/plain").param(0).messageBody().buildMethod()
              .field(uriInfo).context().buildField()
              .setter(setter).context().buildSetter()
              .buildClass();
      getRegistry().addPerRequestResource(resourceclass);


      Client client = ClientBuilder.newClient();
      String path = generateURL("/test");
      WebTarget target = client.target(path);

      String value = target.queryParam("a", "hello").request().get(String.class);
      Assert.assertEquals(value, "hello");

      Response response = target.request().put(Entity.text("hello"));
      Assert.assertEquals(204, response.getStatus());

      getRegistry().removeRegistrations(resourceclass);

   }

   @Test
   public void testSingleton() throws Exception
   {
      Method get = MyResource.class.getMethod("get", String.class);
      Method put = MyResource.class.getMethod("put", String.class);
      Method setter = MyResource.class.getMethod("setHeaders", HttpHeaders.class);
      Field uriInfo = MyResource.class.getDeclaredField("uriInfo");

      ResourceClass resourceclass = ResourceBuilder.rootResource(MyResource.class)
              .method(get).get().path("test").produces("text/plain").param(0).queryParam("a").buildMethod()
              .field(uriInfo).context().buildField()
              .setter(setter).context().buildSetter()
              .buildClass();
      MyResource resource = new MyResource();
      getRegistry().addSingletonResource(resource, resourceclass);

      Client client = ClientBuilder.newClient();
      String path = generateURL("/test");
      WebTarget target = client.target(path);

      String value = target.queryParam("a", "hello").request().get(String.class);
      Assert.assertEquals(value, "hello");
      Assert.assertEquals(1, resource.counter);
      getRegistry().removeRegistrations(resourceclass);

   }

}
