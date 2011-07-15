package org.jboss.resteasy.test.war;

import java.util.Collections;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.test.smoke.MyProvider;
import org.jboss.resteasy.util.HttpResponseCodes;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OAuthTest
{
    
   static final String RequestURL = "http://localhost:9095/oauth-servlet-test/oauth/requestToken";
   static final String AccessURL = "http://localhost:9095/oauth-servlet-test/oauth/accessToken";
   static final String ProtectedURL = "http://localhost:9095/oauth-servlet-test/rest/security";
   
   @Test
   public void testRequestNoParams() throws Exception
   {
      HttpClient client = new HttpClient();
      
      GetMethod method = new GetMethod(RequestURL);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, status);
      method.releaseConnection();
   }

   @Test
   public void testRequestInvalidConsumerSecret() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(getRequestURL(MyProvider.Consumer1Key, "foo"));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
      method.releaseConnection();
   }

   @Test
   public void testRequestInvalidConsumerKey() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(getRequestURL("bar", "foo"));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
      method.releaseConnection();
   }

   @Test
   public void testRequestAllParams() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(getRequestURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      // check that we got all tokens
      Map<String, String> response = getResponse(method);
      Assert.assertEquals(response.size(), 3);
      Assert.assertTrue(response.containsKey(OAuth.OAUTH_TOKEN));
      Assert.assertTrue(response.get(OAuth.OAUTH_TOKEN).length() > 0);
      Assert.assertTrue(response.containsKey(OAuth.OAUTH_TOKEN_SECRET));
      Assert.assertTrue(response.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0);
      Assert.assertTrue(response.containsKey(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM));
      Assert.assertEquals(response.get(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM), "true");

      method.releaseConnection();
   }

   private Map<String, String> getResponse(GetMethod method) throws Exception {
	   return OAuth.newMap(OAuth.decodeForm(method.getResponseBodyAsString()));
   }


   @Test
   public void testAccessNoParams() throws Exception
   {
      HttpClient client = new HttpClient();
      
      GetMethod method = new GetMethod(AccessURL);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, status);
      method.releaseConnection();
   }

   @Test
   public void testAccessAllParams() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(getAccessURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Request1Key, MyProvider.Consumer1Request1Secret, MyProvider.Consumer1Request1Verifier));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      // check that we got all tokens
      Map<String, String> response = getResponse(method);
      Assert.assertEquals(response.size(), 2);
      Assert.assertTrue(response.containsKey(OAuth.OAUTH_TOKEN));
      Assert.assertTrue(response.get(OAuth.OAUTH_TOKEN).length() > 0);
      Assert.assertTrue(response.containsKey(OAuth.OAUTH_TOKEN_SECRET));
      Assert.assertTrue(response.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0);

      method.releaseConnection();
   }

   @Test
   public void testAccessAllParamsAgain() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(getAccessURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Request1Key, MyProvider.Consumer1Request1Secret, MyProvider.Consumer1Request1Verifier));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
      method.releaseConnection();
   }

   @Test
   public void testAccessNonAuthorized() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(getAccessURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Request2Key, MyProvider.Consumer1Request2Secret, "foo"));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, status);
      method.releaseConnection();
   }

	@Test
	public void testProtectedResourceAdminRole() throws Exception
	{
		testProtectedURL("/adminRole", HttpResponseCodes.SC_OK);
	}

	@Test
	public void testProtectedResourceAdminRoleCtx() throws Exception
	{
		testProtectedURL("/adminRoleCtx", HttpResponseCodes.SC_OK);
	}

	@Test
	public void testProtectedResourceUserRoleCtx() throws Exception
	{
		testProtectedURL("/userRoleCtx", HttpResponseCodes.SC_UNAUTHORIZED);
	}
	@Test
	public void testProtectedResourceUserRole() throws Exception
	{
		testProtectedURL("/userRole", HttpResponseCodes.SC_UNAUTHORIZED);
	}

	@Test
   public void testProtectedResourceAdminNameCtx() throws Exception
   {
	   testProtectedURL("/adminNameCtx", HttpResponseCodes.SC_OK);
   }

   @Test
   public void testProtectedResourceUserNameCtx() throws Exception
   {
	   testProtectedURL("/userNameCtx", HttpResponseCodes.SC_UNAUTHORIZED);
   }

   @Test
   public void testProtectedResourceAuthenticationMethod() throws Exception
   {
	   testProtectedURL("/authMethod", HttpResponseCodes.SC_OK);
   }

   private void testProtectedURL(String url, int expectedStatus) throws Exception{
	   HttpClient client = new HttpClient();
	   GetMethod method = new GetMethod(getProtectedURL(url, MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Access1Key, MyProvider.Consumer1Access1Secret));
	   int status = client.executeMethod(method);
	   Assert.assertEquals(expectedStatus, status);
	   method.releaseConnection();
   }

   private String getRequestURL(String consumerKey, String consumerSecret) throws Exception {
	   OAuthMessage message = new OAuthMessage("GET", RequestURL, Collections.<Map.Entry>emptyList());
	   OAuthConsumer consumer = new OAuthConsumer("http://callback.net", consumerKey, consumerSecret, null);
	   OAuthAccessor accessor = new OAuthAccessor(consumer);
	   message.addParameter(OAuth.OAUTH_CALLBACK, consumer.callbackURL);
	   message.addRequiredParameters(accessor);
	   return OAuth.addParameters(message.URL, message.getParameters());
   }

   private String getAccessURL(String consumerKey, String consumerSecret, String requestKey, String requestSecret, String verifier) throws Exception {
	   OAuthMessage message = new OAuthMessage("GET", AccessURL, Collections.<Map.Entry>emptyList());
	   OAuthConsumer consumer = new OAuthConsumer("http://callback.net", consumerKey, consumerSecret, null);
	   OAuthAccessor accessor = new OAuthAccessor(consumer);
	   accessor.requestToken = requestKey;
	   accessor.tokenSecret = requestSecret;
	   message.addParameter(OAuthUtils.OAUTH_VERIFIER_PARAM, verifier);
	   message.addParameter(OAuth.OAUTH_TOKEN, requestKey);
	   message.addRequiredParameters(accessor);
	   return OAuth.addParameters(message.URL, message.getParameters());
   }

   private String getProtectedURL(String url, String consumerKey, String consumerSecret, String accessKey, String accessSecret) throws Exception {
	   OAuthMessage message = new OAuthMessage("GET", ProtectedURL+url, Collections.<Map.Entry>emptyList());
	   OAuthConsumer consumer = new OAuthConsumer("http://callback.net", consumerKey, consumerSecret, null);
	   OAuthAccessor accessor = new OAuthAccessor(consumer);
	   accessor.accessToken = accessKey;
	   accessor.tokenSecret = accessSecret;
	   message.addParameter(OAuth.OAUTH_TOKEN, accessKey);
	   message.addRequiredParameters(accessor);
	   return OAuth.addParameters(message.URL, message.getParameters());
   }

}
