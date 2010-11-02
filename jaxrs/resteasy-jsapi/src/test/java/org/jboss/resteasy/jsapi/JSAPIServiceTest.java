package org.jboss.resteasy.jsapi;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.PrintWriter;

public class JSAPIServiceTest
{

   @Path("foo")
   public static class FooResource
   {
      @GET
      @Path("{id}/bar")
      public String getFoo(@PathParam("id") int id, @QueryParam("queryParm1") String query)
      {
         return "haha";
      }
   }

   @Test
   public void test() throws IOException
   {
      ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
      ResourceMethodRegistry rmr = new ResourceMethodRegistry(providerFactory);
      rmr.addPerRequestResource(FooResource.class);
      ServiceRegistry service = new ServiceRegistry(null, rmr, providerFactory, null);
      PrintWriter printWriter = new PrintWriter(System.out);
      new JSAPIWriter("/base").writeJavaScript("", printWriter, service);
      printWriter.close();
   }
}
