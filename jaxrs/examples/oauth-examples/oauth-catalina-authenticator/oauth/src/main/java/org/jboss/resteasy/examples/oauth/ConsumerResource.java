package org.jboss.resteasy.examples.oauth;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;

import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.HttpResponseCodes;

@Path("consumer")
public class ConsumerResource
{
   private static final String ConsumerRegistrationURL; 
   private static final String RequestTokenURL;
   private static final String TokenAuthorizationURL;
   private static final String AccessTokenURL;
   private static final String EndUserResourceURL;
   
   static {
       Properties props = new Properties();
       try {
           props.load(ConsumerResource.class.getResourceAsStream("/oauth.properties"));
       } catch (Exception ex) {
           throw new RuntimeException("oauth.properties resource is not available");
       }
       ConsumerRegistrationURL = props.getProperty("consumer.registration.url");
       RequestTokenURL = props.getProperty("request.token.url");
       AccessTokenURL = props.getProperty("access.token.url");
       TokenAuthorizationURL = props.getProperty("token.authorization.url");
       EndUserResourceURL = props.getProperty("enduser.resource.url");
   }
   
   private static final String DEFAULT_CONSUMER_ID = "print-resources.com";
   
   @Context
   private UriInfo ui;
   private volatile String consumerSecret;
   private volatile Token requestToken;
   private volatile String endUserScope;
      
   @Path("end-user-service")
   @GET
   public Response providerServiceToEndUser(@QueryParam("scope") String scope) throws Exception {
       endUserScope = scope == null || scope.isEmpty() ? EndUserResourceURL : scope;
       // consumer registration - this will be done earlier in the real cases
       consumerSecret = getSharedSecret(DEFAULT_CONSUMER_ID);
       
       accessWithoutToken(endUserScope);
       
       // request a temporarily request token
       requestToken = getRequestToken(DEFAULT_CONSUMER_ID, consumerSecret, 
                                      getCallbackURI(), endUserScope, "printResources");
       // and redirect the end user to the token authorization URI for this request token
       // be authorized - in the end we'll expect the token verifier
       return Response.status(302).location(
               URI.create(getAuthorizationURL(DEFAULT_CONSUMER_ID, requestToken))).build();
   }

   private void accessWithoutToken(String uri) throws Exception
   {
      ClientRequest request = new ClientRequest(uri + "/resource1");
      ClientResponse<?> response = null;
      try {
         response = request.get();
         if (400 != response.getStatus()) {
            throw new RuntimeException("Consumer has not been authorized yet but already can access the resource");
        }
      } finally {
         response.releaseConnection();
      }
//      
//       HttpClient client = new HttpClient();
//       GetMethod method = new GetMethod(uri + "/resource1");
//       int status = client.executeMethod(method);
//       if (400 != status) {
//           throw new RuntimeException("Consumer has not been authorized yet but already can access the resource");
//       }
   }
   
   
   /**
    * Browser-based redirection works better with GET
    * 
    */
   @Path("token-authorization")
   @GET
   public Response setRequestTokenVerifierUsingGET(@QueryParam(OAuth.OAUTH_TOKEN) String token, 
                                                   @QueryParam(OAuth.OAUTH_VERIFIER) String verifier) throws Exception {
       return doSetRequestTokenVerifier(token, verifier);
   }
   
   @Path("token-authorization")
   @POST
   public Response setRequestTokenVerifierUsingPost(@QueryParam(OAuth.OAUTH_TOKEN) String token, 
                                                    @QueryParam(OAuth.OAUTH_VERIFIER) String verifier) throws Exception {
       return doSetRequestTokenVerifier(token, verifier);
   }
   
   public Response doSetRequestTokenVerifier(String token, String verifier) throws Exception {
       requestToken.setVerifier(verifier);
       
       // exchange the authorized request token for the access token
       Token accessToken = getAccessToken(DEFAULT_CONSUMER_ID, consumerSecret, requestToken);
       
       // try to get the admin space 
       tryAccessEndUserAdminResource(accessToken);
       // and finally use it to access the user resource
       String response = accessEndUserResource(accessToken);
       return Response.ok().type("text/plain").entity(response).build();
   }
   
   private String getCallbackURI() {
       UriBuilder ub = ui.getBaseUriBuilder();
       return ub.path(ConsumerResource.class).path("token-authorization").build().toString();
   }
   
   public String getSharedSecret(String consumerKey) throws Exception
   {
      ClientRequest request = new ClientRequest(ConsumerRegistrationURL);
      request.formParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
      ClientResponse<String> response = null;
      try {
         response = request.post(String.class);
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Registration failed");
         }
         // check that we got all tokens
         Map<String, String> tokens = OAuth.newMap(OAuth.decodeForm(response.getEntity()));
         String secret = tokens.get("xoauth_consumer_secret");
         if (secret == null) {
            throw new RuntimeException("No secret available");
         }
         return secret;
      } finally {
         response.releaseConnection();
      }

//      HttpClient client = new HttpClient();
//      PostMethod method = new PostMethod(ConsumerRegistrationURL);
//      method.addParameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
//      int status = client.executeMethod(method);
//      if (HttpResponseCodes.SC_OK != status) {
//          throw new RuntimeException("Registration failed");
//      }
//      // check that we got all tokens
//      Map<String, String> response = OAuth.newMap(OAuth.decodeForm(method.getResponseBodyAsString()));
//      String secret = response.get("xoauth_consumer_secret");
//      if (secret == null) {
//          throw new RuntimeException("No secret available");
//      }
//      return secret;
   }
   
   public Token getRequestToken(String consumerKey, String consumerSecret, 
                                String callbackURI, String scope, String permission) throws Exception
   {
      String url = getRequestURL(consumerKey, consumerSecret, callbackURI, scope, permission);
      ClientRequest request = new ClientRequest(url);
      ClientResponse<String> response = null;
      try {
         response = request.post(String.class);
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Request token can not be obtained");
         }
         // check that we got all tokens
         Map<String, String> tokens = getResponse(response);
         if (tokens.size() != 3
               || !tokens.containsKey(OAuth.OAUTH_TOKEN)
               || !(tokens.get(OAuth.OAUTH_TOKEN).length() > 0)
               || !tokens.containsKey(OAuth.OAUTH_TOKEN_SECRET)
               || !(tokens.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0)
               || !tokens.containsKey(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM)
               || !tokens.get(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM).equals("true")) {
            throw new RuntimeException("Wrong request token details");
         }

         return new Token(tokens.get(OAuth.OAUTH_TOKEN), tokens.get(OAuth.OAUTH_TOKEN_SECRET));
      } finally {
         response.releaseConnection();
      }
      
//      HttpClient client = new HttpClient();
//      GetMethod method = new GetMethod(
//              getRequestURL(consumerKey, consumerSecret, callbackURI, scope, permission));
//      int status = client.executeMethod(method);
//      if (HttpResponseCodes.SC_OK != status) {
//          throw new RuntimeException("Request token can not be obtained");
//      }
//      // check that we got all tokens
//      Map<String, String> response = getResponse(method);
//      if (response.size() != 3
//          || !response.containsKey(OAuth.OAUTH_TOKEN)
//          || !(response.get(OAuth.OAUTH_TOKEN).length() > 0)
//          || !response.containsKey(OAuth.OAUTH_TOKEN_SECRET)
//          || !(response.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0)
//          || !response.containsKey(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM)
//          || !response.get(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM).equals("true")) {
//          throw new RuntimeException("Wrong request token details");
//      }
//
//      method.releaseConnection();
//      
//      return new Token(response.get(OAuth.OAUTH_TOKEN), response.get(OAuth.OAUTH_TOKEN_SECRET));
   }

   private Map<String, String> getResponse(ClientResponse<String> response) throws Exception {
	   return OAuth.newMap(OAuth.decodeForm(response.getEntity()));
   }


   public Token getAccessToken(String consumerKey, String consumerSecret,
           Token requestToken) throws Exception
   {
      String url = getAccessURL(consumerKey, consumerSecret, 
                                requestToken.getToken(), requestToken.getSecret(),
                                requestToken.getVerifier());
      ClientRequest request = new ClientRequest(url);
      ClientResponse<String> response = null;
      try {
         response = request.post(String.class);
         if (HttpResponseCodes.SC_OK != response.getStatus()) {
            throw new RuntimeException("Request token can not be obtained");
        }
        // check that we got all tokens
        Map<String, String> tokens = getResponse(response);
        if (tokens.size() != 2
            || !tokens.containsKey(OAuth.OAUTH_TOKEN)
            || !(tokens.get(OAuth.OAUTH_TOKEN).length() > 0)
            || !tokens.containsKey(OAuth.OAUTH_TOKEN_SECRET)
            || !(tokens.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0)) {
            throw new RuntimeException("Wrong access token details");
        }
        
        return new Token(tokens.get(OAuth.OAUTH_TOKEN), tokens.get(OAuth.OAUTH_TOKEN_SECRET));
      } finally {
         response.releaseConnection();
      }
      
//      HttpClient client = new HttpClient();
//      GetMethod method = new GetMethod(getAccessURL(consumerKey, consumerSecret, 
//                 requestToken.getToken(), requestToken.getSecret(), requestToken.getVerifier()));
//      int status = client.executeMethod(method);
//      if (HttpResponseCodes.SC_OK != status) {
//          throw new RuntimeException("Request token can not be obtained");
//      }
//      // check that we got all tokens
//      Map<String, String> response = getResponse(method);
//      if (response.size() != 2
//          || !response.containsKey(OAuth.OAUTH_TOKEN)
//          || !(response.get(OAuth.OAUTH_TOKEN).length() > 0)
//          || !response.containsKey(OAuth.OAUTH_TOKEN_SECRET)
//          || !(response.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0)) {
//          throw new RuntimeException("Wrong access token details");
//      }
//      
//      method.releaseConnection();
//      return new Token(response.get(OAuth.OAUTH_TOKEN), response.get(OAuth.OAUTH_TOKEN_SECRET));
   }

   
	public String accessEndUserResource(Token accessToken) throws Exception
	{
	   String url = getEndUserURL("/resource1", DEFAULT_CONSUMER_ID, consumerSecret, accessToken.getToken(), accessToken.getSecret());
	   ClientRequest request = new ClientRequest(url);
	   ClientResponse<String> response = null;
	   try {
	      response = request.post(String.class);
	      if (200 != response.getStatus()) {
	         throw new RuntimeException("Unexpected status");
	      }
	      return response.getEntity();
	   } finally {
	      response.releaseConnection();
	   }
	   
//	    HttpClient client = new HttpClient();
//        GetMethod method = new GetMethod(getEndUserURL("/resource1", DEFAULT_CONSUMER_ID, consumerSecret, accessToken.getToken(), accessToken.getSecret()));
//	    try {
//    	    int status = client.executeMethod(method);
//    	    if (200 != status) {
//    	        throw new RuntimeException("Unexpected status");
//    	    }
//    	    return method.getResponseBodyAsString();
//	    } finally {
//	        method.releaseConnection();
//	    }
   }
	
	public void tryAccessEndUserAdminResource(Token accessToken) throws Exception
    {
       String url = getEndUserURL("/resource2", DEFAULT_CONSUMER_ID, consumerSecret, accessToken.getToken(), accessToken.getSecret());
       ClientRequest request = new ClientRequest(url);
       ClientResponse<?> response = null;
       try {
          response = request.post();
          if (401 != response.getStatus()) {
             throw new RuntimeException("Unexpected status");
          }
       } finally {
          response.releaseConnection();
       }
//        HttpClient client = new HttpClient();
//        GetMethod method = new GetMethod(getEndUserURL("/resource2", DEFAULT_CONSUMER_ID, consumerSecret, accessToken.getToken(), accessToken.getSecret()));
//        try {
//            int status = client.executeMethod(method);
//            if (401 != status) {
//                throw new RuntimeException("Unexpected status");
//            }
//        } finally {
//            method.releaseConnection();
//        }
   }	

   private String getRequestURL(String consumerKey, String consumerSecret, 
                                String callbackURI, String scope, String permission) throws Exception {
	   OAuthMessage message = new OAuthMessage("GET", RequestTokenURL, Collections.<Map.Entry>emptyList());
	   OAuthConsumer consumer = new OAuthConsumer(callbackURI, consumerKey, consumerSecret, null);
	   OAuthAccessor accessor = new OAuthAccessor(consumer);
	   message.addParameter(OAuth.OAUTH_CALLBACK, consumer.callbackURL);
	   message.addParameter("xoauth_scope", scope);
	   message.addParameter("xoauth_permission", permission);
	   message.addRequiredParameters(accessor);
	   return OAuth.addParameters(message.URL, message.getParameters());
   }

   private String getAuthorizationURL(String consumerKey, Token requestToken) throws Exception {
       List<OAuth.Parameter> parameters = new ArrayList<OAuth.Parameter>();
       //parameters.add(new OAuth.Parameter(OAuth.OAUTH_CONSUMER_KEY, consumerKey));
       parameters.add(new OAuth.Parameter(OAuth.OAUTH_TOKEN, requestToken.getToken()));
       
       return OAuth.addParameters(TokenAuthorizationURL, parameters);
   }
   
   private String getAccessURL(String consumerKey, String consumerSecret, String requestKey, String requestSecret, String verifier) throws Exception {
	   OAuthMessage message = new OAuthMessage("GET", AccessTokenURL, Collections.<Map.Entry>emptyList());
	   OAuthConsumer consumer = new OAuthConsumer("http://callback.net", consumerKey, consumerSecret, null);
	   OAuthAccessor accessor = new OAuthAccessor(consumer);
	   accessor.requestToken = requestKey;
	   accessor.tokenSecret = requestSecret;
	   message.addParameter(OAuthUtils.OAUTH_VERIFIER_PARAM, verifier);
	   message.addParameter(OAuth.OAUTH_TOKEN, requestKey);
	   message.addRequiredParameters(accessor);
	   return OAuth.addParameters(message.URL, message.getParameters());
   }

   private String getEndUserURL(String url, String consumerKey, String consumerSecret, String accessKey, String accessSecret) throws Exception {
	   OAuthMessage message = new OAuthMessage("GET", endUserScope + url, Collections.<Map.Entry>emptyList());
	   OAuthConsumer consumer = new OAuthConsumer("http://callback.net", consumerKey, consumerSecret, null);
	   OAuthAccessor accessor = new OAuthAccessor(consumer);
	   accessor.accessToken = accessKey;
	   accessor.tokenSecret = accessSecret;
	   message.addParameter(OAuth.OAUTH_TOKEN, accessKey);
	   message.addRequiredParameters(accessor);
	   return OAuth.addParameters(message.URL, message.getParameters());
   }

   private static class Token {
       private String token;
       private String secret;
       private String verifier;
       
       public Token(String token, String secret) {
           this.token = token;
           this.secret = secret;
       }
       
       public String getToken() {
           return token;
       }
       
       public String getSecret() {
           return secret;
       }

    private void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    private String getVerifier() {
        return verifier;
    }
   }
   
}
