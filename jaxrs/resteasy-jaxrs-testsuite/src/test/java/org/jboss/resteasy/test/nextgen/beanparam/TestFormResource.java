/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.nextgen.beanparam;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * A TestFormResource.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestFormResource extends BaseResourceTest
{
   private static final String SHORT_VALUE_FIELD = "shortValue";

   private static final String INTEGER_VALUE_FIELD = "integerValue";

   private static final String LONG_VALUE_FIELD = "longValue";

   private static final String DOUBLE_VALUE_FIELD = "doubleValue";

   private static final String NAME_FIELD = "name";

   private static final String BOOLEAN_VALUE_FIELD = "booleanValue";

   private static final String TEST_URI = generateURL("/form/42?query=42");

   @SuppressWarnings("unused")
   private static final Logger logger = Logger.getLogger(TestFormResource.class);

   @Path("/form/{id}")
   public interface FormClientProxy
   {
      @Produces(MediaType.APPLICATION_FORM_URLENCODED)
      @POST
      MultivaluedMap<String, String> post(@BeanParam ClientForm form);

      @Produces(MediaType.APPLICATION_FORM_URLENCODED)
      @POST
      String postString(@BeanParam ClientForm form);

   }

   public static class ClientForm2
   {
      @HeaderParam("custom-header")
      protected String foo;


      public String getFoo()
      {
         return foo;
      }

      public void setFoo(String foo)
      {
         this.foo = foo;
      }
   }

   @Path("/myform")
   public interface MyFormProxy
   {
      @POST
      public void post(@BeanParam ClientForm2 form2);
   }

   @Path("/myform")
   public static class MyFormResource
   {
      @GET
      @Path("/server")
      @Produces("application/x-www-form-urlencoded")
      public MultivaluedMap<String, String> retrieveServername()
      {

         MultivaluedMap<String, String> serverMap = new MultivaluedMapImpl<String, String>();
         serverMap.add("servername", "srv1");
         serverMap.add("servername", "srv2");

         return serverMap;
      }

      @POST
      public void post()
      {

      }
   }

   /**
    * FIXME Comment this
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(FormResource.class);
      addPerRequestResource(MyFormResource.class);
   }

   /**
    * RESTEASY-261
    *
    * @throws Exception
    */
   @Test
   public void testMultiValueParam() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      ResteasyWebTarget target = client.target(generateURL("/myform/server"));
      Response response = target.request().get();
      int status = response.getStatus();
      Assert.assertEquals(200, status);
      boolean sv1 = false;
      boolean sv2 = false;
      MultivaluedMap<String, String> form = response.readEntity(new javax.ws.rs.core.GenericType<MultivaluedMap<String, String>>(){});
      Assert.assertEquals(2, form.get("servername").size());
      for (String str : form.get("servername"))
      {
         if (str.equals("srv1")) sv1 = true;
         else if (str.equals("srv2")) sv2 = true;
      }
      Assert.assertTrue(sv1);
      Assert.assertTrue(sv2);
   }

   /**
    * RESTEASY-691
    *
    * @throws Exception
    */
   @Test
   public void testProxy691() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      ResteasyWebTarget target = client.target(generateBaseUrl());
      MyFormProxy proxy = target.proxy(MyFormProxy.class);
      proxy.post(null);
   }

   @Test
   public void testProxy() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      ResteasyWebTarget target = client.target(generateBaseUrl());
      FormClientProxy proxy = target.proxy(FormClientProxy.class);
      ClientForm form = new ClientForm();
      form.setBooleanValue(true);
      form.setName("This is My Name");
      form.setDoubleValue(123.45);
      form.setLongValue(566780L);
      form.setIntegerValue(3);
      form.setShortValue((short) 12345);
      form.setHeaderParam(42);
      form.setQueryParam(42);
      form.setId(42);
      MultivaluedMap<String, String> rtn = proxy.post(form);
      Assert.assertEquals(rtn.getFirst(BOOLEAN_VALUE_FIELD), "true");
      Assert.assertEquals(rtn.getFirst(NAME_FIELD), "This is My Name");
      Assert.assertEquals(rtn.getFirst(DOUBLE_VALUE_FIELD), "123.45");
      Assert.assertEquals(rtn.getFirst(LONG_VALUE_FIELD), "566780");
      Assert.assertEquals(rtn.getFirst(INTEGER_VALUE_FIELD), "3");
      Assert.assertEquals(rtn.getFirst(SHORT_VALUE_FIELD), "12345");
      String str = proxy.postString(form);
      System.out.println("STR: " + str);
      String[] params = str.split("&");
      Map<String, String> map = new HashMap<String, String>();
      for (int i = 0; i < params.length; i++)
      {
         int index = params[i].indexOf('=');
         String key = params[i].substring(0, index).trim();
         String value = params[i].substring(index + 1).trim().replace('+', ' ');
         map.put(key, value);
      }
      Assert.assertEquals(map.get(BOOLEAN_VALUE_FIELD), "true");
      Assert.assertEquals(map.get(NAME_FIELD), "This is My Name");
      Assert.assertEquals(map.get(DOUBLE_VALUE_FIELD), "123.45");
      Assert.assertEquals(map.get(LONG_VALUE_FIELD), "566780");
      Assert.assertEquals(map.get(INTEGER_VALUE_FIELD), "3");
      Assert.assertEquals(map.get(SHORT_VALUE_FIELD), "12345");
   }

   @Test
   public void testFormResource() throws Exception
   {      
      InputStream in = null;
      try
      {
         ResteasyClient client = new ResteasyClient();
         ResteasyWebTarget target = client.target(TEST_URI);
         Invocation.Builder request = target.request();
         request.header("custom-header", "42");
         Form form = new Form().param(BOOLEAN_VALUE_FIELD, "true")
                               .param(NAME_FIELD, "This is My Name")
                               .param(DOUBLE_VALUE_FIELD, "123.45")
                               .param(LONG_VALUE_FIELD, "566780")
                               .param(INTEGER_VALUE_FIELD, "3")
                               .param(SHORT_VALUE_FIELD, "12345");
         Response response = request.post(Entity.form(form));
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         String contentType = response.getHeaderString("content-type");
         Assert.assertEquals("application/x-www-form-urlencoded", contentType);
         InputStream responseStream = response.readEntity(InputStream.class);
         in = new BufferedInputStream(responseStream);
         String formData = readString(in);
         String[] keys = formData.split("&");
         Map<String, String> values = new HashMap<String, String>();
         for (String pair : keys)
         {
            int index = pair.indexOf('=');
            if (index < 0)
            {
               values.put(URLDecoder.decode(pair, "UTF-8"), null);
            }
            else if (index > 0)
            {
               values.put(URLDecoder.decode(pair.substring(0, index), "UTF-8"), URLDecoder.decode(pair
                     .substring(index + 1), "UTF-8"));
            }
         }
         Assert.assertEquals(values.get(BOOLEAN_VALUE_FIELD), "true");
         Assert.assertEquals(values.get(NAME_FIELD), "This is My Name");
         Assert.assertEquals(values.get(DOUBLE_VALUE_FIELD), "123.45");
         Assert.assertEquals(values.get(LONG_VALUE_FIELD), "566780");
         Assert.assertEquals(values.get(INTEGER_VALUE_FIELD), "3");
      }
      finally
      {
         if (in != null)
         {
            in.close();
         }
      }
   }
}
