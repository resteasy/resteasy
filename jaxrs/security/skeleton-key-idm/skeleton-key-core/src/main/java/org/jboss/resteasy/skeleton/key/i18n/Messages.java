package org.jboss.resteasy.skeleton.key.i18n;

import org.jboss.logging.annotations.Message;
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
   int BASE = 16500;

   @Message(id = BASE + 0, value = "code parameter was null")
   String codeParameterWasNull(); 
   
   @Message(id = BASE + 5, value = "Failed to load keystore")
   String failedToLoadKeystore();
   
   @Message(id = BASE + 10, value = "Failed to load truststore")
   String failedToLoadTruststore();
   
   @Message(id = BASE + 15, value = "Failed to verify token")
   String failedToVerifyToken();
   
   @Message(id = BASE + 20, value = "Must set 'realm' in config")
   String mustSetRealmInConfig();
   
   @Message(id = BASE + 25, value = "You must set the realm-public-key")
   String mustSetRealmPublicKey();

   @Message(id = BASE + 30, value = "OAuth error: %s")
   String oAuthError(String error);
   
   @Message(id = BASE + 35, value = "******************** redirect_uri: %s")
   String redirectUri(String uri);

   @Message(id = BASE + 40, value = "state cookie not set")
   String stateCookieNotSet();
   
   @Message(id = BASE + 45, value = "state parameter invalid")
   String stateParameterInvalid();

   @Message(id = BASE + 50, value = "state parameter was null")
   String stateParameterWasNull();
   
   @Message(id = BASE + 55, value = "Token audience doesn't match domain")
   String tokenAudienceDoesntMatchDomain();
   
   @Message(id = BASE + 60, value = "Token is not active.")
   String tokenNotActive();
   
   @Message(id = BASE + 65, value = "Token signature not validated")
   String tokenSignatureNotValidated();   

   @Message(id = BASE + 70, value = "Token user was null")
   String tokenUserNull();
   
   @Message(id = BASE + 75, value = "Unknown error when getting access token")
   String unknownErrorGettingAccessToken();
}
