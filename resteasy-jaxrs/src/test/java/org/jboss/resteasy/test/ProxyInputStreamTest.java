package org.jboss.resteasy.test;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <a href="https://jira.jboss.org/jira/browse/RESTEASY-351">RESTEASY-351</a>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyInputStreamTest extends BaseResourceTest
{
   @Path("/test")
   public static class MyResourceImpl
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello world";
      }

   }

   @Path("/test")
   public static interface MyResource
   {
      @GET
      @Produces("text/plain")
      public InputStream get();

   }

   @BeforeClass
   public static void setUp() throws Exception
   {
      addPerRequestResource(MyResourceImpl.class);
   }

   private AtomicLong counter = new AtomicLong();

   @Test
   public void testInputStream() throws Exception
   {
      MyResource proxy = ProxyFactory.create(MyResource.class, "http://localhost:8081");
      InputStream is = proxy.get();
      byte[] bytes = ReadFromStream.readFromStream(100, is);
      is.close();
      String str = new String(bytes);
      Assert.assertEquals("hello world", str);
   }
}