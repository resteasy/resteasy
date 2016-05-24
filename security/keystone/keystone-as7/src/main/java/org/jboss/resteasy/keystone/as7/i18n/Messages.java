package org.jboss.resteasy.keystone.as7.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 29, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 14500;
   
   @Message(id = BASE + 0, value = "Bad Signature")
   String badSignature();
   
   @Message(id = BASE + 5, value = "Bad Token")
   String badToken();
   
   @Message(id = BASE + 10, value = "Could not get certificate from keyStore")
   String couldNotGetCertificate();
   
   @Message(id = BASE + 15, value = "No trust store found")
   String noTrustStoreFound();
   
   @Message(id = BASE + 20, value = "Security Domain: %s")
   String securityDomain(String sd);
   
   @Message(id = BASE + 25, value = "The JSSE security domain %s is not valid. All authentication using this login module will fail!")
   String securityDomainNotValid(String sd);
   
   @Message(id = BASE + 30, value = "Token expired")
   String tokenExpired();
   
   @Message(id = BASE + 35, value = "Token project id doesn't match")
   String tokenProjectIdDoesntMatch();
   
   @Message(id = BASE + 40, value = "Unable to find the securityDomain named: %s")
   String unableToFindSecurityDomain(String sd);
}
