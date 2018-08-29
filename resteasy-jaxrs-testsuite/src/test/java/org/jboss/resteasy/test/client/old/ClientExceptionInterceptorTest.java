package org.jboss.resteasy.test.client.old;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:lincoln@ocpsoft.com">Lincoln Baxter, III</a>
 */
public class ClientExceptionInterceptorTest extends BaseResourceTest
{
   private static final String RESPONSE_OK = "RESPONSE OK";

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestResource.class);
      getProviderFactory().getClientErrorInterceptors().clear();
   }

   @Test
   public void testNoExceptionEncountered() throws Exception
   {
      getProviderFactory().addClientErrorInterceptor(new TestNoExceptionInterceptor());
      getProviderFactory().addClientErrorInterceptor(new TestThrowExceptionInterceptor());
      TestService service = ProxyFactory.create(TestService.class, generateURL("/"));
      String result = service.get();
      assertEquals(RESPONSE_OK, result);
   }

   @Test(expected = ClientResponseFailure.class)
   public void testExceptionEncounteredAndIgnoredIsRethrown() throws Exception
   {
      getProviderFactory().addClientErrorInterceptor(new TestNoExceptionInterceptor());
      TestService service = ProxyFactory.create(TestService.class, generateURL("/"));
      service.getBadUrl();
   }

   @Test(expected = TestException.class)
   public void testExceptionEncounteredAndNewExceptionThrown() throws Exception
   {
      getProviderFactory().addClientErrorInterceptor(new TestThrowExceptionInterceptor());
      TestService service = ProxyFactory.create(TestService.class, generateURL("/"));
      service.getBadUrl();
   }

   @Test
   public void testExceptionInterceptorsChain() throws Exception
   {
      TestNoExceptionInterceptor handler = new TestNoExceptionInterceptor();
      getProviderFactory().addClientErrorInterceptor(handler);
      getProviderFactory().addClientErrorInterceptor(new TestThrowExceptionInterceptor());
      TestService service = ProxyFactory.create(TestService.class, generateURL("/"));

      try
      {
         service.getBadUrl();
         fail();
      }
      catch (TestException e)
      {
         assertTrue(handler.isHandled());
      }
   }

   /*
     * Test utility classes
     */
   @Path("/test")
   public static class TestResource
   {
      @GET
      @Path("/get")
      @Produces("text/plain")
      public String get()
      {
         return RESPONSE_OK;
      }
   }

   @Path("/test")
   public interface TestService
   {
      @GET
      @Path("/get")
      @Produces("text/plain")
      String get();

      @GET
      @Path("/foo")
      @Produces("text/plain")
      String getBadUrl();
   }

   public static class TestThrowExceptionInterceptor implements ClientErrorInterceptor
   {
      public void handle(ClientResponse<?> response) throws RuntimeException
      {
         throw new TestException();
      }
   }

   public static class TestNoExceptionInterceptor implements ClientErrorInterceptor
   {
      private boolean handled = false;

      public void handle(ClientResponse<?> response) throws RuntimeException
      {
         handled = true;
      }

      public boolean isHandled()
      {
         return handled;
      }
   }

   @SuppressWarnings("serial")
   public static class TestException extends RuntimeException
   {
   }
}