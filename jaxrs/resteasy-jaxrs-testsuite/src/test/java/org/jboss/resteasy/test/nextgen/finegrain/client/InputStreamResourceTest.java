package org.jboss.resteasy.test.nextgen.finegrain.client;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class InputStreamResourceTest
{

   private static ResteasyDeployment deployment;

   @Path("/inputStream")
   @Produces("text/plain")
   @Consumes("text/plain")
   public interface Client
   {
      @GET
      String getAsString();

      @GET
      Response getAsInputStream();

      @POST
      Response postInputStream(InputStream is);

      @POST
      Response postString(String s);
   }

   @Path("/inputStream")
   public static class Service
   {
      String result = "hello";

      @GET
      @Produces("text/plain")
      public InputStream get()
      {
         return new ByteArrayInputStream(result.getBytes());
      }

      @POST
      @Consumes("text/plain")
      public void post(InputStream is) throws IOException
      {
         result = new String(ReadFromStream.readFromStream(1024, is));
      }

   }

   @BeforeClass
   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addSingletonResource(new Service());
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testClientResponse() throws Exception
   {
      ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
      Client client = ProxyBuilder.builder(Client.class, resteasyClient.target(generateBaseUrl())).build();

      Assert.assertEquals("hello", client.getAsString());
      Response is = client.getAsInputStream();
      Assert.assertEquals("hello", new String(ReadFromStream.readFromStream(
              1024, is.readEntity(InputStream.class))));
      is.close();
      client.postString("new value");
      Assert.assertEquals("new value", client.getAsString());
      client.postInputStream(new ByteArrayInputStream("new value 2".getBytes()));
      Assert.assertEquals("new value 2", client.getAsString());
      resteasyClient.close();

   }
}