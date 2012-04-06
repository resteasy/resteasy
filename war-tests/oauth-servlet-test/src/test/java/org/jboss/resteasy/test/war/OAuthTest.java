package org.jboss.resteasy.test.war;

import java.util.Collections;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;

import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
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
      ClientRequest request = new ClientRequest(RequestURL);
      ClientResponse<?> response = request.get();
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void testRequestInvalidConsumerSecret() throws Exception
   {
      ClientRequest request = new ClientRequest(getRequestURL(MyProvider.Consumer1Key, "foo"));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void testRequestInvalidConsumerKey() throws Exception
   {
      ClientRequest request = new ClientRequest(getRequestURL("bar", "foo"));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void testRequestAllParams() throws Exception
   {
      ClientRequest request = new ClientRequest(getRequestURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret));
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      // check that we got all tokens
      Map<String, String> tokens = getResponse(response.getEntity());
      Assert.assertEquals(tokens.size(), 3);
      Assert.assertTrue(tokens.containsKey(OAuth.OAUTH_TOKEN));
      Assert.assertTrue(tokens.get(OAuth.OAUTH_TOKEN).length() > 0);
      Assert.assertTrue(tokens.containsKey(OAuth.OAUTH_TOKEN_SECRET));
      Assert.assertTrue(tokens.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0);
      Assert.assertTrue(tokens.containsKey(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM));
      Assert.assertEquals(tokens.get(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM), "true");
   }

   private Map<String, String> getResponse(String response) throws Exception {
	   return OAuth.newMap(OAuth.decodeForm(response));
   }


   @Test
   public void testAccessNoParams() throws Exception
   {
      ClientRequest request = new ClientRequest(AccessURL);
      ClientResponse<?> response = request.get();
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      response.releaseConnection();
   }

   @Test
   public void testAccessAllParams() throws Exception
   {
      ClientRequest request = new ClientRequest(getAccessURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Request1Key, MyProvider.Consumer1Request1Secret, MyProvider.Consumer1Request1Verifier));
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      // check that we got all tokens
      Map<String, String> tokens = getResponse(response.getEntity());
      Assert.assertEquals(tokens.size(), 2);
      Assert.assertTrue(tokens.containsKey(OAuth.OAUTH_TOKEN));
      Assert.assertTrue(tokens.get(OAuth.OAUTH_TOKEN).length() > 0);
      Assert.assertTrue(tokens.containsKey(OAuth.OAUTH_TOKEN_SECRET));
      Assert.assertTrue(tokens.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0);
   }

   @Test
   public void testAccessAllParamsAgain() throws Exception
   {
      ClientRequest request = new ClientRequest(getAccessURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Request1Key, MyProvider.Consumer1Request1Secret, MyProvider.Consumer1Request1Verifier));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
      response.releaseConnection();   }

   @Test
   public void testAccessNonAuthorized() throws Exception
   {
      ClientRequest request = new ClientRequest(getAccessURL(MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Request2Key, MyProvider.Consumer1Request2Secret, "foo"));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
      response.releaseConnection();
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
      ClientRequest request = new ClientRequest(getProtectedURL(url, MyProvider.Consumer1Key, MyProvider.Consumer1Secret, MyProvider.Consumer1Access1Key, MyProvider.Consumer1Access1Secret));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(expectedStatus, response.getStatus());
      response.releaseConnection();
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
