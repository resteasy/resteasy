package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Regression435Test extends BaseResourceTest
{
   @Path("/test")
   public interface MyTest
   {
      @POST
      @Consumes("text/plain")
      public void postIt(String msg);
   }

   public static class MyTestResource implements MyTest
   {
      public void postIt(String msg)
      {
         System.out.println("HERE: " + msg);
         throw new WebApplicationException(401);
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(MyTestResource.class);
   }

   @Test
   public void testMe() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      MyTest proxy = client.target(TestPortProvider.generateURL("")).proxy(MyTest.class);
      try
      {
         proxy.postIt("hello");
         Assert.fail();
      }
      catch (NotAuthorizedException e)
      {
         Assert.assertEquals(401, e.getResponse().getStatus());
      }
      client.close();
   }

}
