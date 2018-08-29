package org.jboss.resteasy.test.client.core;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

public class ClientErrorInterceptorTest extends BaseResourceTest
{

   public static class MyResourceImpl implements MyResource
   {
      public String get()
      {
         return "hello world";
      }

      public String error()
      {
         Response r = Response.status(404).type("text/plain").entity("there was an error").build();
         throw new NoLogWebApplicationException(r);
      }

      @Override
      public void update(String id, String obj)
      {
         Response r = Response.status(404).type("text/plain").entity("there was an error").build();
         throw new NoLogWebApplicationException(r);
      }
   }

   @Path("/test")
   public interface MyResource
   {
      @GET
      @Produces("text/plain")
      String get();

      @GET
      @Path("error")
      @Produces("text/plain")
      String error();

      @PUT
      @Path("{id}")
      @Consumes(MediaType.APPLICATION_XML)
      void update(@PathParam("id") String id, String obj);
   }

   @BeforeClass
   public static void setUp() throws Exception
   {
      addPerRequestResource(MyResourceImpl.class);
   }

   public static class MyClienteErrorInterceptor implements ClientErrorInterceptor
   {
      @Override
      public void handle(ClientResponse<?> response) throws RuntimeException
      {
         String errorMessage = response.getEntity(String.class);
         throw new MyException(errorMessage);
      }
   }

   public static class MyException extends RuntimeException
   {
      private static final long serialVersionUID = 1L;

      public MyException(String message)
      {
         super(message);
      }
   }

   @Test
   public void testStreamClosedWhenGetEntity() throws Exception
   {
      HttpClient httpClient = new DefaultHttpClient();
      ClientExecutor clientExecutor = new ApacheHttpClient4Executor(httpClient);

      ResteasyProviderFactory pf = ResteasyProviderFactory.getInstance();
      pf.addClientErrorInterceptor(new MyClienteErrorInterceptor());

      MyResource proxy = ProxyFactory.create(MyResource.class, URI.create(generateBaseUrl()), clientExecutor, pf);

      try
      {
         proxy.error();
         Assert.fail();
      }
      catch (MyException e)
      {
         Assert.assertEquals("there was an error", e.getMessage());
      }
   }

   @Test
   public void testStreamClosedWhenGetEntityForVoid() throws Exception
   {
      HttpClient httpClient = new DefaultHttpClient();
      ClientExecutor clientExecutor = new ApacheHttpClient4Executor(httpClient);

      ResteasyProviderFactory pf = ResteasyProviderFactory.getInstance();
      pf.addClientErrorInterceptor(new MyClienteErrorInterceptor());

      MyResource proxy = ProxyFactory.create(MyResource.class, URI.create(generateBaseUrl()), clientExecutor, pf);

      try
      {
         proxy.update("1", "hello");
         Assert.fail();
      }
      catch (MyException e)
      {
         Assert.assertEquals("there was an error", e.getMessage());
      }
   }
}
