package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;


import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.FormParam;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SmokeTest
{
   @Test
   public void testNoDefaultsResource() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/resteasy/rest/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
   }

   @Test
   public void testFormParam()
   {
      ResteasyProviderFactory.initializeInstance();
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      final FormResource client = ProxyFactory.create(FormResource.class, "http://localhost:8080/resteasy/rest");
      final String result = client.postForm("value");
      Assert.assertEquals(result, "value");
   }

   @Path("/")
   public static interface FormResource
   {
      @POST
      @Path("formtestit")
      public String postForm(@FormParam("value") String value);
   }
}
