package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormUrlEncodedTest
{
   private static Dispatcher dispatcher;
   private static final Logger LOG = Logger.getLogger(FormUrlEncodedTest.class);

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Path("/")
   public static class SimpleResource
   {
      @Path("/simple")
      @POST
      public StreamingOutput post(@QueryParam("hello") String abs, InputStream entityStream) throws IOException
      {
         Assert.assertNull(abs);
         final InputStream is = entityStream;
         return new StreamingOutput()
         {
            public void write(OutputStream output) throws IOException
            {
               LOG.info("WITHIN STREAMING OUTPUT!!!!");
               int c;
               while ((c = is.read()) != -1)
               {
                  output.write(c);
               }
            }
         };
      }

      @Path("/form")
      @POST
      @Produces("application/x-www-form-urlencoded")
      @Consumes("application/x-www-form-urlencoded")
      public MultivaluedMap<String, String> post(MultivaluedMap<String, String> form)
      {
         Assert.assertEquals("world", form.getFirst("hello"));
         return form;
      }

      @Path("/form/twoparams")
      @POST
      @Produces("application/x-www-form-urlencoded")
      @Consumes("application/x-www-form-urlencoded")
      public MultivaluedMap<String, String> post2Parameters(MultivaluedMap<String, String> form)
      {
         Assert.assertEquals(2, form.size());
         Assert.assertEquals("world", form.getFirst("hello"));
         Assert.assertEquals("mama", form.getFirst("yo"));
         return form;
      }

      @Path("/RESTEASY-109")
      @POST
      public void post109(MultivaluedMap<String, String> form)
      {
         Assert.assertEquals(form.getFirst("name"), "jon");
         Assert.assertEquals(form.getFirst("address1"), "123 Main St");
         Assert.assertEquals(form.getFirst("address2"), "");
         Assert.assertEquals(form.getFirst("zip"), "12345");
      }

   }

   /**
    * Testing  JIRA RESTEASY-109
    */
   @Test
   public void testResteasy109()
   {
      ClientRequest request = new ClientRequest(generateURL("/RESTEASY-109"));
      request.body(MediaType.APPLICATION_FORM_URLENCODED,
                   "name=jon&address1=123+Main+St&address2=&zip=12345");
      try
      {
         ClientResponse<?> response = request.post();
         Assert.assertEquals(204, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   //@Test
   public void testQueryParamIsNull()
   {
      ClientRequest request = new ClientRequest(generateURL("/simple"));
      request.formParameter("hello", "world");
      try
      {
         ClientResponse<String> response = request.post(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("hello=world", response.getEntity());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   //@Test
   public void testPost()
   {
      ClientRequest request = new ClientRequest(generateURL("/form"));
      request.formParameter("hello", "world");
      try
      {
         ClientResponse<String> response = request.post(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("hello=world", response.getEntity());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   //@Test
   public void testPostTwoParameters()
   {
      ClientRequest request = new ClientRequest(generateURL("/form/twoparams"));
      request.formParameter("hello", "world");
      request.formParameter("yo", "mama");
      try
      {
         ClientResponse<String> response = request.post(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String body = response.getEntity();
         Assert.assertTrue(body.indexOf("hello=world") != -1);
         Assert.assertTrue(body.indexOf("yo=mama") != -1);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Path("/")
   public interface TestProxy
   {
      @Path("/form")
      @POST
      @Produces("application/x-www-form-urlencoded")
      @Consumes("application/x-www-form-urlencoded")
      String post(MultivaluedMap<String, String> form);

      @Path("/form")
      @POST
      @Produces("application/x-www-form-urlencoded")
      @Consumes("application/x-www-form-urlencoded")
      MultivaluedMap<String, String> post2(MultivaluedMap<String, String> form);
   }

   //@Test
   public void testProxy()
   {
      TestProxy proxy = ProxyFactory.create(TestProxy.class, generateBaseUrl());
      MultivaluedMapImpl<String, String> form = new MultivaluedMapImpl<String, String>();
      form.add("hello", "world");
      String body = proxy.post(form);
      Assert.assertEquals("hello=world", body);

      MultivaluedMap<String, String> rtn = proxy.post2(form);
      Assert.assertEquals(rtn.getFirst("hello"), "world");
   }

}
