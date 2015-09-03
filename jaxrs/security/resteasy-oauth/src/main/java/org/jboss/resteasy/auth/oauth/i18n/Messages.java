package org.jboss.resteasy.auth.oauth.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 31, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 15500;

   @Message(id = BASE + 0, value = "Access token")
   String accessToken();

   @Message(id = BASE + 5, value = "Access token URL: %s")
   String accessTokenUrl(String url);

   @Message(id = BASE + 10, value = "Adding parameter {0} => {1}", format=Format.MESSAGE_FORMAT)
   String addingParameter(String parameter, String value);

   @Message(id = BASE + 15, value = "All OK")
   String allOK();

   @Message(id = BASE + 20, value = "Arguments should be name=value*")
   String argumentsShouldBeNameValue();

   @Message(id = BASE + 25, value = " class %s could not be instantiated")
   String classCouldNotBeInstantiated(String className);
   
   @Message(id = BASE + 30, value = " class %s must be an instance of OAuthProvider")
   String classMustBeInstanceOAuthProvider(String className);

   @Message(id = BASE + 35, value = " class %s not found")
   String classNotFound(String className);

   @Message(id = BASE + 40, value = "Consumer is invalid")
   String consumerIsInvalid();

   @Message(id = BASE + 45, value = "Consumer registration")
   String consumerRegistration();

   @Message(id = BASE + 50, value = "Consumer token authorization request")
   String consumerTokenAuthorizationRequest();
   
   @Message(id = BASE + 55, value = "doFilter")
   String doFilter();

   @Message(id = BASE + 60, value = "Do not use this method")
   String doNotUseThisMethod();

   @Message(id = BASE + 65, value = "Error [{0}]: {1}", format=Format.MESSAGE_FORMAT)
   String errorHttpCode(int httpCode, String message);
   
   @Message(id = BASE + 70, value = "Exception ")
   String exception();

   @Message(id = BASE + 75, value = "Filtering {0} {1}", format=Format.MESSAGE_FORMAT)
   String filteringMethod(String method, String url);

   @Message(id = BASE + 80, value = "Invalid customer key")
   String invalidCustomerKey();
   
   @Message(id = BASE + 85, value = "Invalid timestamp")
   String invalidTimestamp();

   @Message(id = BASE + 90, value = "Invalid timestamp %s")
   String invalidTimestampLong(long timestamp);

   @Message(id = BASE + 95, value = "Invalid timestamp %s")
   String invalidTimestampString(String timestamp);
   
   @Message(id = BASE + 100, value = "Invalid verifier code for token %s")
   String invalidVerifierCode(String requestToken);
   
   @Message(id = BASE + 105, value = "Loading OAuth Filter")
   String loadingOAuthFilter();

   @Message(id = BASE + 110, value = "Loading OAuthProvider: %s")
   String loadingOAuthProvider(String className);   

   @Message(id = BASE + 115, value = "Loading OAuth Servlet")
   String loadingOAuthServlet();   

   @Message(id = BASE + 120, value = "No such access key %s")
   String noSuchAccessKey(String accessKey);

   @Message(id = BASE + 125, value = "No such consumer key %s")
   String noSuchConsumerKey(String consumerKey);
   
   @Message(id = BASE + 130, value = "No such request key %s")
   String noSuchRequestKey(String requestKey);
   
   @Message(id = BASE + 135, value = "No such request token %s")
   String noSuchRequestToken(String requestToken);

   @Message(id = BASE + 140, value = "OAuthProvider should not return null")
   String oAuthProviderShouldNotReturnNull();

   @Message(id = BASE + 145, value = "OAuthServlet loaded")
   String oAuthServletLoaded();
   
   @Message(id = BASE + 150, value = " parameter required")
   String parameterRequired();
   
   @Message(id = BASE + 155, value = "Parameters present")
   String parametersPresent();

   @Message(id = BASE + 160, value = "Query %s")
   String queryString(String queryString);

   @Message(id = BASE + 165, value = "Request token")
   String requestToken();
   
   @Message(id = BASE + 170, value = "This request token has already been authorized")
   String requestTokenAlreadyAuthorized();
   
   @Message(id = BASE + 175, value = "Request token URL: %s")
   String requestTokenUrl(String url);

   @Message(id = BASE + 180, value = "Serving %s")
   String serving(String pathinfo);
   
   @Message(id = BASE + 185, value = "Token has not been authorized")
   String tokenHasNotBeenAuthorized();
   
   @Message(id = BASE + 190, value = "UTF8 encoding should be supported")
   String utf8EncodingShouldBeSupported();
   
   @Message(id = BASE + 195, value = "Wrong callback URI")
   String wrongCallbackURI();

   @Message(id = BASE + 200, value = "Wrong URI Scope")
   String wrongURIScope();
}
