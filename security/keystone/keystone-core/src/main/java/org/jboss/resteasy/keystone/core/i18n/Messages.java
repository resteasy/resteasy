package org.jboss.resteasy.keystone.core.i18n;

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
   int BASE = 15000;
   
   @Message(id = BASE + 0, value = "Certificate is null")
   String certificateNull();
   
   @Message(id = BASE + 5, value = "Failed to unmarshall")
   String failedToUnmarshall();
   
   @Message(id = BASE + 10, value = "idp is null")
   String idpNull();
   
   @Message(id = BASE + 15, value = "Keystore path invalid: %s")
   String keystorePathInvalid(String path);
   
   @Message(id = BASE + 20, value = "need to specify skeleton.key.infinispan.cache.name")
   String needToSpecifyCacheName();
   
   @Message(id = BASE + 25, value = "No key store provided.")
   String noKeystoreProvided();
   
   @Message(id = BASE + 30, value = "password is nul")
   String passwordNull();

   @Message(id = BASE + 35, value = "Private Key is null")
   String privateKeyNull();

   @Message(id = BASE + 40, value = "privateKey or certificate not set for this operation")
   String privateKeyOrCertificateNotSet();
   
   @Message(id = BASE + 45, value = "username is null")
   String usernameNull();
}
