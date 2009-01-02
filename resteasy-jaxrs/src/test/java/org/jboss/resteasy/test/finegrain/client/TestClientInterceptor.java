package org.jboss.resteasy.test.finegrain.client;

import static org.jboss.resteasy.test.TestPortProvider.createClientRequest;
import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.ClientInterceptor;
import org.jboss.resteasy.client.core.ClientResponseImpl;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.smoke.SimpleResource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClientInterceptor
{
   private static Dispatcher dispatcher;

   private final class MyClientInterceptor implements ClientInterceptor
   {
      private int postExecuted = 0;

      public void postExecute(ClientResponseImpl clientResponseImpl)
      {
         postExecuted++;
      }

      public int getPostExecuted()
      {
         return postExecuted;
      }

      public void postUnMarshalling(ClientResponseImpl clientResponseImpl)
      {
      }

      public void preBaseMethodConstruction(ClientResponseImpl clientResponseImpl)
      {
      }

      public void preExecute(ClientResponseImpl clientResponseImpl)
      {
      }
   }

   @Path("/")
   public interface Client
   {
      @GET
      @Path("basic")
      @Produces("text/plain")
      ClientResponse<String> getBasic();

      @GET
      @Path("basic")
      ClientResponse getBasic2();

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      void putBasic(String body);

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      Response.Status putBasicReturnCode(String body);

      @GET
      @Path("queryParam")
      @Produces("text/plain")
      ClientResponse<String> getQueryParam(@QueryParam("param") String param);

      @GET
      @Path("uriParam/{param}")
      @Produces("text/plain")
      ClientResponse<Integer> getUriParam(@PathParam("param") int param);

      @GET
      @Path("header")
      ClientResponse<Void> getHeader();

      @GET
      @Path("basic")
      ClientResponse<byte[]> getBasicBytes();

      @GET
      @Path("error")
      ClientResponse<String> getError();
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testClientInterceptor() throws Exception
   {
      MyClientInterceptor interceptor = new MyClientInterceptor();
      Collection<ClientInterceptor> interceptors = Arrays.<ClientInterceptor> asList(interceptor);
      final HttpClient httpClient = new HttpClient();
      final URI uri = new URI(generateBaseUrl());
      Client client = ProxyFactory.create(Client.class, uri, httpClient, ResteasyProviderFactory.getInstance(),
            interceptors);
      client.getBasic();
      Assert.assertEquals(1, interceptor.getPostExecuted());
      client.getBasic();
      Assert.assertEquals(2, interceptor.getPostExecuted());
      createClientRequest("/basic").interceptor(interceptor).get();
      Assert.assertEquals(3, interceptor.getPostExecuted());
   }
}
