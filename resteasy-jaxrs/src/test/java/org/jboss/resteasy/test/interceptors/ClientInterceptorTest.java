package org.jboss.resteasy.test.interceptors;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientInterceptorTest extends BaseResourceTest
{

   @Path("/")
   public static interface IGZIP
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      public String getText();

   }

   @Path("/")
   public static class GZIPService
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      public Response getText(@Context HttpHeaders headers)
      {
         String acceptEncoding = headers.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
         System.out.println(acceptEncoding);
         Assert.assertEquals("gzip, deflate", acceptEncoding);
         return Response.ok(new StreamingOutput()
         {
            public void write(OutputStream outputStream) throws IOException, WebApplicationException
            {
               System.out.println("WRITING*********");
               GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
               PrintStream writer = new PrintStream(gzip);
               writer.print("HELLO WORLD");
               gzip.finish();
            }
         }).header("Content-Encoding", "gzip").build();
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(GZIPService.class);
   }


   @Test
   public void testProxy() throws Exception
   {
      IGZIP proxy = ProxyFactory.create(IGZIP.class, generateBaseUrl());
      Assert.assertEquals("HELLO WORLD", proxy.getText());
   }

   @Test
   public void testRequest() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/text"));
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals("HELLO WORLD", response.getEntity());

   }


}
