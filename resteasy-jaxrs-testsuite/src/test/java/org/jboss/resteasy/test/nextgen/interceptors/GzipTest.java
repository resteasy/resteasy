package org.jboss.resteasy.test.nextgen.interceptors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GzipTest extends BaseResourceTest
{

   private static final Logger LOG = Logger.getLogger(GzipTest.class);

   @Path("/")
   public interface IGZIP
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      String getText();

      @GET
      @Path("encoded/text")
      @GZIP
      String getGzipText();

      @GET
      @Path("encoded/text/error")
      @GZIP
      String getGzipErrorText();

   }

   @Path("/")
   public static class GZIPService
   {
      @GET
      @Path("text")
      @Produces("text/plain")
      public Response getText(@Context HttpHeaders headers)
      {
         /* Can't test this anymore because TCK expects that no accept encoding is set by default
         String acceptEncoding = headers.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
         LOG.info(acceptEncoding);
         Assert.assertEquals("gzip, deflate", acceptEncoding);
         */
         return Response.ok("HELLO WORLD").header("Content-Encoding", "gzip").build();
      }

      @GET
      @Path("encoded/text")
      @GZIP
      public String getGzipText()
      {
         return "HELLO WORLD";
      }

      @GET
      @Path("bytes")
      @GZIP
      @Produces("text/plain")
      public byte[] getBytes()
      {
         return "HELLO WORLD".getBytes();
      }

      @GET
      @Path("error")
      @GZIP
      @Produces({"application/json;charset=UTF-8"})
      public StreamingOutput getTest()
      {
         return new StreamingOutput()
         {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException
            {
               throw new WebApplicationException(405);
            }
         };
      }

      @GET
      @Path("encoded/text/error")
      @GZIP
      public String getGzipErrorText()
      {
         throw new WebApplicationException(
                 Response.status(500).entity("Hello").type("text/plain").build()
         );
      }

      @PUT
      @Consumes("text/plain")
      @Path("stream")
      public void putStream(InputStream is) throws Exception
      {
         byte[] bytes = ReadFromStream.readFromStream(1024, is);
         String str = new String(bytes);
         Assert.assertEquals("hello world", str);
      }

      @PUT
      @Consumes("text/plain")
      @Path("text")
      public void putText(String text) throws Exception
      {
         Assert.assertEquals("hello world", text);
      }
   }

   @Before
   public void setUp() throws Exception
   {
      ResteasyProviderFactory.getInstance().registerProvider(AcceptEncodingGZIPInterceptor.class);
      ResteasyProviderFactory.getInstance().registerProvider(GZIPEncodingInterceptor.class);
      ResteasyProviderFactory.getInstance().registerProvider(GZIPDecodingInterceptor.class);
      addPerRequestResource(GZIPService.class);
   }

   @Test
   public void testRawStreams() throws Exception
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPEncodingInterceptor.EndableGZIPOutputStream os = new GZIPEncodingInterceptor.EndableGZIPOutputStream(baos);
      os.write("hello world".getBytes());
      os.finish();
      os.close();

      byte[] bytes1 = baos.toByteArray();
      LOG.info(bytes1.length);
      LOG.info(new String(bytes1));
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes1);
      GZIPDecodingInterceptor.FinishableGZIPInputStream is = new GZIPDecodingInterceptor.FinishableGZIPInputStream(bis);
      byte[] bytes = ReadFromStream.readFromStream(1024, is);
      is.finish();
      String str = new String(bytes);
      Assert.assertEquals("hello world", str);


   }


   @Test
   public void testProxy() throws Exception
   {
      ResteasyClient client = getClient();
      ResteasyWebTarget target = client.target(generateBaseUrl());
      IGZIP proxy = target.proxy(IGZIP.class);
      Assert.assertEquals("HELLO WORLD", proxy.getText());
      Assert.assertEquals("HELLO WORLD", proxy.getGzipText());

      // resteasy-651
      try
      {
         String error = proxy.getGzipErrorText();
         Assert.fail("unreachable");
      }
      catch (WebApplicationException failure)
      {
         Assert.assertEquals(500, failure.getResponse().getStatus());
         String txt = (String) failure.getResponse().readEntity(String.class);
         Assert.assertEquals("Hello", txt);
      }
      finally
      {
         client.close();
      }
   }

   /**
    * RESTEASY-692
    *
    * @throws Exception
    */
   @Test
   public void testContentLength() throws Exception
   {
      ResteasyClient client = getClient();
      {
         WebTarget target = client.target(TestPortProvider.generateURL("/text"));
         Response response = target.request().get();
         Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));
         String cl = response.getHeaderString("Content-Length");
         if (cl != null)
         {
            // make sure the content length is greater than 11 because this will be a gzipped encoding
         Assert.assertTrue(response.getLength() > 11);
         }
      }
      {
         WebTarget target = client.target(TestPortProvider.generateURL("/bytes"));
         Response response = target.request().acceptEncoding("gzip").get();
         String cl = response.getHeaderString("Content-Length");
         if (cl != null)
         {
            // make sure the content length is greater than 11 because this will be a gzipped encoding
            int i = response.getLength();
            LOG.info("***");
            LOG.info("Content-Length: " + i);
            LOG.info("***");
            Assert.assertTrue(i > 11);
         }
         Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));
      }
      client.close();
   }

   @Test
   public void testRequestError() throws Exception
   {
      ResteasyClient client = getClient();
      WebTarget target = client.target(TestPortProvider.generateURL("/error"));
      Response response = target.request().get();
      Assert.assertEquals(405, response.getStatus());
      client.close();

   }

   @Test
   public void testPutStream() throws Exception
   {
      ResteasyClient client = getClient();
      WebTarget target = client.target(TestPortProvider.generateURL("/stream"));
      Response res = target.request().header("Content-Encoding", "gzip").put(Entity.text("hello world"));
      Assert.assertEquals(204, res.getStatus());
      client.close();
   }

   @Test
   public void testPutText() throws Exception
   {
      ResteasyClient client = getClient();
      WebTarget target = client.target(TestPortProvider.generateURL("/text"));
      Response res = target.request().header("Content-Encoding", "gzip").put(Entity.text("hello world"));
      Assert.assertEquals(204, res.getStatus());
      client.close();
   }

   @Test
   public void testRequest() throws Exception
   {
      ResteasyClient client = getClient();
      WebTarget target = client.target(TestPortProvider.generateURL("/text"));
      String val = target.request().get(String.class);
      Assert.assertEquals("HELLO WORLD", val);
      client.close();

   }

   @Test
   public void testRequest2() throws Exception
   {
      ResteasyClient client = getClient();
      WebTarget target = client.target(TestPortProvider.generateURL("/encoded/text"));
      Response response = target.request().get();
      Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));
      client.close();

   }

   @Test
   public void testWasZipped() throws Exception
   {
      // test that it was zipped by running it through Apache HTTP Client which does not automatically unzip

      HttpClient client = new DefaultHttpClient();
      {
         HttpGet get = new HttpGet(TestPortProvider.generateURL("/encoded/text"));
         get.addHeader("Accept-Encoding", "gzip, deflate");
         HttpResponse response = client.execute(get);
         Assert.assertEquals(200, response.getStatusLine().getStatusCode());
         Assert.assertEquals("gzip", response.getFirstHeader("Content-Encoding").getValue());

         // test that it is actually zipped
         String entity = EntityUtils.toString(response.getEntity());
         LOG.info(entity);
         Assert.assertNotSame(entity, "HELLO WORLD");
      }


      {
         HttpGet get = new HttpGet(TestPortProvider.generateURL("/text"));
         get.addHeader("Accept-Encoding", "gzip, deflate");
         HttpResponse response = client.execute(get);
         Assert.assertEquals(200, response.getStatusLine().getStatusCode());
         Assert.assertEquals("gzip", response.getFirstHeader("Content-Encoding").getValue());

         // test that it is actually zipped
         String entity = EntityUtils.toString(response.getEntity());
         Assert.assertNotSame(entity, "HELLO WORLD");
      }
      client.getConnectionManager().shutdown();
   }

   @Test
   public void testWithoutAcceptEncoding() throws Exception
   {
      // test that if there is no accept-encoding: gzip header that result isn't encoded

      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet(TestPortProvider.generateURL("/encoded/text"));
      HttpResponse response = client.execute(get);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      Assert.assertNull(response.getFirstHeader("Content-Encoding"));

      // test that it is actually zipped
      String entity = EntityUtils.toString(response.getEntity());
      Assert.assertEquals(entity, "HELLO WORLD");
      client.getConnectionManager().shutdown();

   }

   private ResteasyClient getClient()
   {
      // register gzip explicitly
      ResteasyProviderFactory rpf = new ResteasyProviderFactory();
      RegisterBuiltin.register(rpf);
      rpf.registerProvider(AcceptEncodingGZIPInterceptor.class);
      rpf.registerProvider(GZIPEncodingInterceptor.class);
      rpf.registerProvider(GZIPDecodingInterceptor.class);
      return new ResteasyClientBuilder().providerFactory(rpf).build();
   }

}
