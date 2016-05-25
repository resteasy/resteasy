package org.jboss.resteasy.security.doseta.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.resteasy.security.doseta.DKIMSignature;

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
   int BASE = 13500;

   @Message(id = BASE + 0, value = "Body hashes do not match.")
   String bodyHashesDoNotMatch();

   @Message(id = BASE + 5, value = "Certificate nor public key properties set")
   String certificateNorPublicKeySet();
   
   @Message(id = BASE + 10, value = "The certificate object was not set.")
   String certificateObjectNotSet();

   @Message(id = BASE + 15, value = ">>>> Check DNS: %s")
   String checkDNS(String alias);

   @Message(id = BASE + 20, value = "Could not find a message body reader for type: %s")
   String couldNotFindMessageBodyReader(String className);
   
   @Message(id = BASE + 25, value = "Could not find PublicKey for DKIMSignature %s")
   String couldNotFindPublicKey(DKIMSignature signature);
   
   @Message(id = BASE + 30, value = ">>>> DNS found record: %s")
   String dnsRecordFound(String record);

   @Message(id = BASE + 35, value = "domain attribute is required in header to find a key.")
   String domainAttributeIsRequired();

   @Message(id = BASE + 40, value = "Expected value ''{0}'' got ''{1}'' for attribute ''{2}''", format=Format.MESSAGE_FORMAT)
   String expectedValue(String expectedValue, String value, String attribute);

   @Message(id = BASE + 45, value = "Failed to find public key in DNS %s")
   String failedToFindPublicKey(String alias);

   @Message(id = BASE + 50, value = "Failed to find writer for type: %s")
   String failedToFindWriter(String className);

   @Message(id = BASE + 55, value = "Failed to parse body hash (bh)")
   String failedToParseBodyHash();

   @Message(id = BASE + 60, value = "Failed to sign")
   String failedToSign();

   @Message(id = BASE + 65, value = "Failed to verify signature.")
   String failedToVerifySignature(); 
   
   @Message(id = BASE + 70, value = "Failed to verify signatures:")
   String failedToVerifySignatures();
 
   @Message(id = BASE + 75, value = "Malformed %s header")
   String malformedSignatureHeader(String signature);
   
   @Message(id = BASE + 80, value = "No key to verify with.")
   String noKeyToVerifyWith();

   @Message(id = BASE + 85, value = "No p entry in text record.")
   String noPEntry();
   
   @Message(id = BASE + 90, value = "pem: %s")
   String pem(String pem);

   @Message(id = BASE + 95, value = "private key is null, cannot sign")
   String privateKeyIsNull();

   @Message(id = BASE + 100, value = "Public key is null.")
   String publicKeyIsNull();

   @Message(id = BASE + 105, value = "Signature expired")
   String signatureExpired();

   @Message(id = BASE + 110, value = "Signature is stale")
   String signatureIsStale();
   
   @Message(id = BASE + 115, value = "There was no body hash (bh) in header")
   String thereWasNoBodyHash();
   
   @Message(id = BASE + 120, value = "There was no %s header")
   String thereWasNoSignatureHeader(String signature);

   @Message(id = BASE + 125, value = "Unable to find header {0} {1} to sign header with", format=Format.MESSAGE_FORMAT)
   String unableToFindHeader(String header, String index);

   @Message(id = BASE + 130, value = "Unable to find key to sign message. Repository returned null. ")
   String unableToFindKey();

   @Message(id = BASE + 135, value = "Unable to find key store in path: %s")
   String unableToFindKeyStore(String path);

   @Message(id = BASE + 140, value = "Unable to locate a private key to sign message, repository is null.")
   String unableToLocatePrivateKey();

   @Message(id = BASE + 145, value = "Unsupported algorithm %s")
   String unsupportedAlgorithm(String algorithm);

   @Message(id = BASE + 150, value = "Unsupported key type: %s")
   String unsupportedKeyType(String type);
}
