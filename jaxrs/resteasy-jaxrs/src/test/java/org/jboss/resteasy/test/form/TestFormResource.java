/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.form;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

   private static final Logger logger = LoggerFactory.getLogger(TestFormResource.class);

   @Path("/form/{id}")
   public interface FormClientProxy
   {
      @Produces(MediaType.APPLICATION_FORM_URLENCODED)
      @POST
      MultivaluedMap<String, String> post(@Form ClientForm form);
   }

   /**
    * FIXME Comment this
    *
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(FormResource.class);
   }

   @Test
   public void testProxy() throws Exception
   {
      FormClientProxy proxy = ProxyFactory.create(FormClientProxy.class, generateBaseUrl());
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
   }

   @Test
   public void testFormResource() throws Exception
   {
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(TEST_URI);
      method.addRequestHeader("custom-header", "42");
      method.addParameter(BOOLEAN_VALUE_FIELD, "true");
      method.addParameter(NAME_FIELD, "This is My Name");
      method.addParameter(DOUBLE_VALUE_FIELD, "123.45");
      method.addParameter(LONG_VALUE_FIELD, "566780");
      method.addParameter(INTEGER_VALUE_FIELD, "3");
      method.addParameter(SHORT_VALUE_FIELD, "12345");
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);
      InputStream response = method.getResponseBodyAsStream();
      BufferedInputStream in = new BufferedInputStream(response);
      String contentType = method.getResponseHeader("content-type").getValue();
      Assert.assertEquals("application/x-www-form-urlencoded", contentType);
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

}
