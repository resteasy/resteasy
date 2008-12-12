package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.FormParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.test.smoke.Customer;

import java.io.StringReader;


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
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = new PutMethod("http://localhost:8080/basic-integration-test/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/queryParam");
         NameValuePair[] params = {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/uriParam/1234");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("1234", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         // I'm testing unknown content-length here
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/xml");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         String result = method.getResponseBodyAsString();
         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         Customer cust = (Customer)ctx.createUnmarshaller().unmarshal(new StringReader(result));
         Assert.assertEquals("Bill Burke", cust.getName());
         method.releaseConnection();
      }
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/locating/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = new PutMethod("http://localhost:8080/basic-integration-test/locating/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/locating/queryParam");
         NameValuePair[] params = {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/basic-integration-test/locating/uriParam/1234");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("1234", method.getResponseBodyAsString());
         method.releaseConnection();
      }
   }

   /*
   public static Test suite() throws Exception
   {
      System.out.println("***********");
      System.out.println(System.getProperty("jbosstest.deploy.dir"));
      return getDeploySetup(SmokeTest.class, "basic-integration-test.war");
   }
   */

   @Test
   public void testFormParam()
   {
      ResteasyProviderFactory.initializeInstance();
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      final FormResource client = ProxyFactory.create(FormResource.class, "http://localhost:8080/basic-integration-test");
      String result = client.postForm("value");
      Assert.assertEquals(result, "value");
      MultivaluedMap<String, String> rtn = new MultivaluedMapImpl<String, String>();
      rtn.add("value", "value");
      result = client.putForm(rtn);
      Assert.assertEquals(result, "value");
   }

   @Path("/")
   public static interface FormResource
   {
      @POST
      @Path("formtestit")
      public String postForm(@FormParam("value") String value);

      @PUT
      @Path("formtestit")
      @Consumes("application/x-www-form-urlencoded")
      public String putForm(MultivaluedMap<String, String> value);
   }
}
