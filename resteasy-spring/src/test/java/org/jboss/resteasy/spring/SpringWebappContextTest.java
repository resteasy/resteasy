package org.jboss.resteasy.spring;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SpringWebappContextTest
{

   private static final String BASE_URL = "http://somehost";
   private static final String CONTEXT_PATH = "/spring-powered";
   private static final String PATH = "/echo";
   private static final String CONFIG_PATH = "/WEB-INF/web.xml";
   private static final String EXPECTED_URI = BASE_URL + CONTEXT_PATH + PATH + "/uri";
   private static final String EXPECTED_HEADERS = BASE_URL + CONTEXT_PATH + PATH + "/headers"
         + ":text/plain";
   protected ServletRunner runner;

   @Before
   public void setUpServlet() throws Exception
   {
      InputStream config = getClass().getResourceAsStream(CONFIG_PATH);
      runner = new ServletRunner(config, CONTEXT_PATH);
      HttpUnitOptions.setExceptionsThrownOnErrorStatus(true);
   }

   @Test
   public void testGetUri() throws Exception
   {
      doTestGet(PATH + "/uri", EXPECTED_URI, null);
   }

   @Test
   public void testGetHeaders() throws Exception
   {
      doTestGet(PATH + "/headers", EXPECTED_HEADERS, null);
   }

   @Test
   public void testParamsDontStick() throws Exception
   {
      // Test that the parameters given to the first request
      // doesn't stick for the second request:
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("param", "0");
      doTestGet(PATH + "/uri", EXPECTED_URI + "?param=0", parameters);
      parameters.put("param", "1");
      doTestGet(PATH + "/uri", EXPECTED_URI + "?param=1", parameters);
   }

   @Test
   public void testConcurrent() throws Exception
   {
      // ensure concurrent invocations see different injected values
      Thread uri = new Thread(new Runnable()
      {
         public void run()
         {
            for (int i = 0; i < 10; i++)
            {
               try
               {
                  doTestGet(PATH + "/uri", EXPECTED_URI, null);
               }
               catch (Exception e)
               {
                  fail(e.toString());
               }
            }
         }
      });
      Thread headers = new Thread(new Runnable()
      {
         public void run()
         {
            for (int i = 0; i < 10; i++)
            {
               try
               {
                  doTestGet(PATH + "/headers", EXPECTED_HEADERS, null);
               }
               catch (Exception e)
               {
                  fail(e.toString());
               }
            }
         }
      });
      uri.start();
      headers.start();
      uri.join();
      headers.join();
   }

   private void doTestGet(String context, String expectedReponsePattern, Map<String, String> parameters) throws Exception
   {
      ServletUnitClient client = runner.newClient();
      WebRequest request = new GetMethodWebRequest(BASE_URL + CONTEXT_PATH + context);
      request.setHeaderField("Accept", "text/plain");

           if (parameters != null) {
              for (Map.Entry<String, String> entry : parameters.entrySet()) {
            request.setParameter(entry.getKey(), entry.getValue());
              }
      }

      verify(client.getResponse(request), 200, expectedReponsePattern);
   }

   private void verify(WebResponse response, int expectedStatus, String expectedResponsePattern)
         throws Exception
   {
      assertEquals("unexpected response code", expectedStatus, response.getResponseCode());
      if (expectedResponsePattern != null)
      {
         String respStr = ProviderHelper.readString(response.getInputStream());
         assertTrue("unexpected response: " + respStr + ", no match for: "
               + expectedResponsePattern, respStr.indexOf(expectedResponsePattern) != -1);
      }
   }
}
