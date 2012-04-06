package org.jboss.resteasy.test.jboss;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AppConfigTest
{
   private void _test(HttpClient client, String uri, String body)
   {
      {
         GetMethod method = new GetMethod(uri);
         try
         {
            method.addRequestHeader("Accept", "text/quoted");
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
      _test(client, "http://localhost:8080/application-config-test/my", "\"hello\"");
   }
}
