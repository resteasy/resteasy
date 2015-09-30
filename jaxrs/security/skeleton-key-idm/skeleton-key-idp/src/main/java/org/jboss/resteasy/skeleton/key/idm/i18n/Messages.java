package org.jboss.resteasy.skeleton.key.idm.i18n;

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
   int BASE = 17000;
   
   @Message(id = BASE + 0, value = "Your account is not enabled")
   String accountIsNotEnabled(); 

   @Message(id = BASE + 5, value = "auth")
   String auth();
   
   @Message(id = BASE + 10, value = "Auth error")
   String authError();
   
   @Message(id = BASE + 15, value = "caller principal not matched")
   String callerPrincipalNotMatched();
   
   @Message(id = BASE + 20, value = "client_id not specified")
   String clientIdNotSpecified();
   
   @Message(id = BASE + 25, value = "client not found")
   String clientNotFound();

   @Message(id = BASE + 30, value = "code")
   String code();

   @Message(id = BASE + 35, value = "Code is expired")
   String codeIsExpired();

   @Message(id = BASE + 40, value = "Code not found")
   String codeNotFound();
   
   @Message(id = BASE + 45, value = "code not specified")
   String codeNotSpecified();
   
   @Message(id = BASE + 50, value = "Could not find user")
   String couldNotFindUser();

   @Message(id = BASE + 55, value = "Credential mismatch")
   String credentialMismatch();

   @Message(id = BASE + 60, value = "Failed to verify signature")
   String failedToVerifySignature();
   
   @Message(id = BASE + 65, value = "grant")
   String grant();

   @Message(id = BASE + 70, value = "Grant Access")
   String grantAccess();

   @Message(id = BASE + 75, value = "Grant Request For ")
   String grantRequestFor();
   
   @Message(id = BASE + 80, value = "Known client not authorized for the requested scope.")
   String knownClientNotAuthorized();
   
   @Message(id = BASE + 85, value = "Known client not authorized to access this realm.")
   String knownClientNotAuthorizedToAccessRealm();

   @Message(id = BASE + 90, value = "Known client not authorized to request a user login.")
   String knownClientNotAuthorizedToRequestUserLogin();

   @Message(id = BASE + 95, value = "Login")
   String login();

   @Message(id = BASE + 100, value = "Login For ")
   String loginFor();

   @Message(id = BASE + 105, value = "Missing required user credential")
   String missingRequiredUserCredential();

   @Message(id = BASE + 110, value = "No realm admin users defined for realm")
   String noRealmAdminUsersDefined(); 
   
   @Message(id = BASE + 115, value = "No realm admin users are enabled or have appropriate credentials")
   String noRealmAdminUsersEnabled();

   @Message(id = BASE + 120, value = "No realms with that name")
   String noRealmsWithThatName();
   
   @Message(id = BASE + 125, value = "No resource role for role mapping")
   String noResourceRoleForRoleMapping();
   
   @Message(id = BASE + 130, value = "No users declared for role mapping")
   String noUsersDeclaredForRoleMapping();
   
   @Message(id = BASE + 135, value = "No users declared for role mapping surrogate")
   String noUsersDeclaredForRoleMappingSurrogate();
   
   @Message(id = BASE + 140, value = "Not valid user")
   String notValidUser();

   @Message(id = BASE + 145, value = "public key")
   String publicKey();
   
   @Message(id = BASE + 150, value = "Realm")
   String realm();

   @Message(id = BASE + 155, value = "Realm credential requirements not defined")
   String realmCredentialRequirementsNotDefined();

   @Message(id = BASE + 160, value = "realm is not enabled")
   String realmIsNotEnabled();

   @Message(id = BASE + 165, value = "Realm not enabled")
   String realmNotEnabled();
   
   @Message(id = BASE + 170, value = "realm not found")
   String realmNotFound();

   @Message(id = BASE + 175, value = "Realms")
   String realms();
   
   @Message(id = BASE + 180, value = "Requester not enabled")
   String requesterNotEnabled();
   
   @Message(id = BASE + 185, value = "Resource: ")
   String resource();

   @Message(id = BASE + 190, value = "Roles:")
   String roles();

   @Message(id = BASE + 195, value = "Security Alert")
   String securityAlert();
   
   @Message(id = BASE + 200, value = "There is only an 'admin' role for realms")
   String thereIsOnlyAdminRole();

   @Message(id = BASE + 205, value = "A Third Party is requesting access to the following resources")
   String thirdPartyIsRequestingAccess();

   @Message(id = BASE + 210, value = "To Authorize, please login below")
   String toAuthorizePleaseLogin();

   @Message(id = BASE + 215, value = "Token expired")
   String tokenExpired();

   @Message(id = BASE + 220, value = "Unable to authenticate, try again")
   String unableToAuthenticate();

   @Message(id = BASE + 225, value = "Unable to verify code signature")
   String unableToVerifyCodeSignature();

   @Message(id = BASE + 230, value = "Security Alert</h1><p>Unknown client trying to get access to your account.")
   String unknownClientTryingToAccess();

   @Message(id = BASE + 235, value = "User is not enabled")
   String userIsNotEnabled();

   @Message(id = BASE + 240, value = "user not found")
   String userNotFound();

   @Message(id = BASE + 245, value = "Username: ")
   String username();

   @Message(id = BASE + 250, value = "You are not authorized for the requested scope.")
   String youAreNotAuthorizedForRequestedScope();  
}
