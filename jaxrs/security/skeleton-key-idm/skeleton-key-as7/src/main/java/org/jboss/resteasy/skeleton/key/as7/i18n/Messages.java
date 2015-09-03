package org.jboss.resteasy.skeleton.key.as7.i18n;

import javax.ws.rs.core.MediaType;

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
   int BASE = 16000;

   @Message(id = BASE + 0, value = "Access code expired")
   String accessCodeExpired();

   @Message(id = BASE + 5, value = "<< adminLogout")
   String adminLogout();

   @Message(id = BASE + 10, value = "We're ALREADY LOGGED IN!!!")
   String alreadyLoggedIn();

   @Message(id = BASE + 15, value = "Auth error")
   String authError();   

   @Message(id = BASE + 20, value = "authenticate userSessionManage.login(): %s")
   String authenticateUserSession(String name);

   @Message(id = BASE + 25, value = "remoteLogout: bearer auth failed")
   String bearerAuthFailed();

   @Message(id = BASE + 30, value = "<--- Begin oauthAuthenticate")
   String beginOauthAuthenticate();

   @Message(id = BASE + 35, value = "--- build redirect")
   String buildRedirect();

   @Message(id = BASE + 40, value = "Challenged")
   String challenged();

   @Message(id = BASE + 45, value = "Code is expired")
   String codeIsExpired();

   @Message(id = BASE + 50, value = "Content-Type header: %s")
   String contentTypeHeader(String mediaType);

   @Message(id = BASE + 55, value = "cookie: %s")
   String cookie(String stateCookie);

   @Message(id = BASE + 60, value = "does not have login or client role permission for access token request")
   String doesNotHaveLoginOrClientPermission();

   @Message(id = BASE + 65, value = "does not have login permission")
   String doesNotHaveLoginPermission();

   @Message(id = BASE + 70, value = "<--- end oauthAuthenticate")
   String endOAuthAuthenticate();
   
   @Message(id = BASE + 75, value = "Failed to authenticate client_id")
   String failedToAuthenticateClientId();
   
   @Message(id = BASE + 80, value = "failed to forward")
   String failedToForward();
   
   @Message(id = BASE + 85, value = "Failed to load keystore")
   String failedToLoadKeystore();
   
   @Message(id = BASE + 90, value = "Failed to load truststore")
   String failedToLoadTruststore();
   
   @Message(id = BASE + 95, value = "Failed to log out")
   String failedToLogout();

   @Message(id = BASE + 100, value = "failed to turn code into token")
   String failedToTurnCodeIntoToken();

   @Message(id = BASE + 105, value = "Failed to verify signature")
   String failedToVerifySignature();

   @Message(id = BASE + 110, value = "Failed to verify token")
   String failedToVerifyToken();

   @Message(id = BASE + 115, value = "failed verification of token")
   String failedVerificationOfToken();

   @Message(id = BASE + 120, value = "found session for user")
   String foundSessionForUser();

   @Message(id = BASE + 125, value = "invalidating session for user: %s")
   String invalidatingSessionForUser(String user);
   
   @Message(id = BASE + 130, value = "--- invoke: %s")
   String invoke(String uri);

   @Message(id = BASE + 135, value = "logging out: %s")
   String loggingOut(String resource);

   @Message(id = BASE + 140, value = "logoutUser: %s")
   String logoutUser(String user);
   
   @Message(id = BASE + 145, value = "media type: %s")
   String mediaType(MediaType mediaType);

   @Message(id = BASE + 150, value = "You have not declared a keystore or public key")
   String mustDeclareKeystoreOrPublicKey();
   
   @Message(id = BASE + 155, value = "You must define the login-role in your config file")
   String mustDefineLoginRole();

   @Message(id = BASE + 160, value = "You must define the oauth-client-role in your config file")
   String mustDefineOauthClientRole();
   
   @Message(id = BASE + 165, value = "Must define realm-key-alias")
   String mustDefineRealmKeyAlias();

   @Message(id = BASE + 170, value = "Must set client-id to use with auth server")
   String mustSetClientId();
   
   @Message(id = BASE + 175, value = "You must specify auth-url")
   String mustSpecifyAuthUrl();
   
   @Message(id = BASE + 180, value = "You must specify code-url")
   String mustSpecifyCodeUrl();
   
   @Message(id = BASE + 185, value = "No access code: %s")
   String noAccessCode(String code);
   
   @Message(id = BASE + 190, value = "No certificates provided by jboss web to verify the caller")
   String noCertificatesProvidedByJBossWeb();
   
   @Message(id = BASE + 195, value = "No oauth redirect query parameter set")
   String noOauthRedirectQueryParameterSet();
   
   @Message(id = BASE + 200, value = "no session for user: %s")
   String noSessionForUser(String user);
   
   @Message(id = BASE + 205, value = "No state cookie")
   String noStateCookie();

   @Message(id = BASE + 210, value = "No trusted certificates in token")
   String noTrustedCertificates();

   @Message(id = BASE + 215, value = "not equal client")
   String notEqualClient();

   @Message(id = BASE + 220, value = "not equal redirect")
   String notEqualRedirect();  

   @Message(id = BASE + 225, value = "OAuth %s")
   String oAuthError(String error);
   
   @Message(id = BASE + 230, value = "queryParam: %s")
   String queryParam(String state);

   @Message(id = BASE + 235, value = "remote logged in already")
   String remoteLoggedInAlready();

   @Message(id = BASE + 240, value = "->> remoteLogout: ")
   String remoteLogout();

   @Message(id = BASE + 245, value = "Restore of original request failed")
   String restoreOfOriginalRequestFailed();

   @Message(id = BASE + 250, value = "restoreRequest")
   String restoreRequest();

   @Message(id = BASE + 255, value = "remoteLogout: role failure")
   String roleFailure();

   @Message(id = BASE + 260, value = "--- sign access code")
   String signAccessCode();

   @Message(id = BASE + 265, value = "SSL is required")
   String sslIsRequired();

   @Message(id = BASE + 270, value = "state parameter invalid")
   String stateParameterInvalid();

   @Message(id = BASE + 275, value = "state parameter was null")
   String stateParameterWasNull();

   @Message(id = BASE + 280, value = "Token expired")
   String tokenExpired();
   
   @Message(id = BASE + 285, value = "token not active")
   String tokenNotActive();

   @Message(id = BASE + 290, value = "Unable to verify code signature")
   String unableToVerifyCodeSignature();

   @Message(id = BASE + 295, value = "userSessionManage.login: %s")
   String userSessionManageLogin(String userName);

   @Message(id = BASE + 300, value = "Verification succeeded!")
   String verificationSucceeded();
}
