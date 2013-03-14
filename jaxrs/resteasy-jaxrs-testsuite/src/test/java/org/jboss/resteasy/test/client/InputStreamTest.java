package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InputStreamTest  extends BaseResourceTest
{
   @Path("/")
   public static class InputStreamResource
   {
      @Path("test")
      @Produces("text/plain")
      @GET
      public String get() { return "hello world"; }
   }

   @Path("/")
   public interface InputStreamInterface
   {
      @Path("test")
      @Produces("text/plain")
      @GET
      public InputStream get();

   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(InputStreamResource.class);
   }

   @Test
   public void testInputStream() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();

      InputStream is  = client.target(generateURL("/test")).request().get(InputStream.class);
      byte[] buf = new byte[1024];
      int read = is.read(buf);
      String str = new String(buf, 0, read);
      Assert.assertEquals("hello world", str);
      System.out.println(str);
      is.close();

      InputStreamInterface proxy = client.target(generateURL("/")).proxy(InputStreamInterface.class);
      is = proxy.get();
      read = is.read(buf);
      str = new String(buf, 0, read);
      Assert.assertEquals("hello world", str);
      is.close();

      client.close();
   }

}
