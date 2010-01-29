package org.jboss.resteasy.jsapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

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
   public void test() throws IOException{
      ResourceMethodRegistry rmr = new ResourceMethodRegistry(ResteasyProviderFactory
            .getInstance());
      rmr.addPerRequestResource(FooResource.class);
      MetaDataService service = new MetaDataService(rmr);
//      CharArrayWriter charArrayWriter = new CharArrayWriter();
      PrintWriter printWriter = new PrintWriter(System.out);
      List<MethodMetaData> methodMetaData = service.getMethodMetaData();
      new JSAPIWriter("/base").writeJavaScript("", printWriter, methodMetaData);
      printWriter.close();
   }
}
