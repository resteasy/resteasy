package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExtensionTest
{
   private void _test(HttpClient client, String uri, String body)
   {
      {
         GetMethod method = new GetMethod(uri);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            Assert.assertEquals(body, method.getResponseBodyAsString());
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }


   @Test
   public void testIt()
   {
      HttpClient client = new HttpClient();
      _test(client, "http://localhost:8080/application-config-test/extension.xml", "xml");
      _test(client, "http://localhost:8080/application-config-test/extension.html.en", "html");
      _test(client, "http://localhost:8080/application-config-test/extension.en.html", "html");
      _test(client, "http://localhost:8080/application-config-test/extension/stuff.old.en.txt", "plain");
      _test(client, "http://localhost:8080/application-config-test/extension/stuff.en.old.txt", "plain");
      _test(client, "http://localhost:8080/application-config-test/extension/stuff.en.txt.old", "plain");
   }

   @Test
   public void testError()
   {
      HttpClient client = new HttpClient();
      {
         GetMethod method = new GetMethod("http://localhost:8080/application-config-test/extension.junk");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_NOT_FOUND);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
}