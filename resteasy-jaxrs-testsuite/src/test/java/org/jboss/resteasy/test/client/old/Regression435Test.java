package org.jboss.resteasy.test.client.old;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Regression435Test extends BaseResourceTest
{

   private static final Logger LOG = Logger.getLogger(Regression435Test.class);

   @Path("/test")
   public interface MyTest
   {
      @POST
      @Consumes("text/plain")
      void postIt(String msg);
   }

   public static class MyTestResource implements MyTest
   {
      public void postIt(String msg)
      {
         LOG.info("HERE: " + msg);
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
      MyTest proxy = ProxyFactory.create(MyTest.class, TestPortProvider.generateURL(""));
      try
      {
         proxy.postIt("hello");
      }
      catch (ClientResponseFailure e)
      {
         Assert.assertEquals(401, e.getResponse().getStatus());
      }
   }

}
