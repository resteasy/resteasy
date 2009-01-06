package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.util.HttpResponseCodes;


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
         GetMethod method = new GetMethod("http://localhost:8080/ejb-integration-war/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = new PutMethod("http://localhost:8080/ejb-integration-war/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(204, status);
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/ejb-integration-war/queryParam");
         NameValuePair[] params = {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/ejb-integration-war/uriParam/1234");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("1234", method.getResponseBodyAsString());
         method.releaseConnection();
      }
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8080/ejb-integration-war/locating/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = new PutMethod("http://localhost:8080/ejb-integration-war/locating/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(204, status);
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/ejb-integration-war/locating/queryParam");
         NameValuePair[] params = {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8080/ejb-integration-war/locating/uriParam/1234");
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
      return getDeploySetup(SmokeTest.class, "ejb-integration-war.war");
   }
   */
}
