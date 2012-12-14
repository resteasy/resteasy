package org.jboss.resteasy.skeleton.key;

import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jwt.JsonSerialization;

import java.io.IOException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RSATokenVerifier
{
   public static SkeletonKeyTokenVerification verify(Principal principal,
                                                     String tokenString, ResourceMetadata metadata) throws VerificationException
   {
      return verify(tokenString, metadata, principal);
   }
   public static SkeletonKeyTokenVerification verify(X509Certificate[] userCerts,
                                                     String tokenString, ResourceMetadata metadata) throws VerificationException
   {
      return verify(tokenString, metadata, userCerts);
   }
   public static SkeletonKeyTokenVerification verify(String tokenString, ResourceMetadata metadata, Object caller) throws VerificationException
   {
      PublicKey realmKey = metadata.getRealmKey();
      String realm = metadata.getRealm();
      String resource = metadata.getResourceName();
      JWSInput input = new JWSInput(tokenString);
      boolean verified = false;
      try
      {
         verified = RSAProvider.verify(input, realmKey);
      }
      catch (Exception ignore)
      {

      }
      if (!verified) throw new VerificationException("Token signature not validated");

      SkeletonKeyToken token = null;
      try
      {
         token = JsonSerialization.fromBytes(SkeletonKeyToken.class, input.getContent());
      }
      catch (IOException e)
      {
         throw new VerificationException(e);
      }
      if (!token.isActive())
      {
         throw new VerificationException("Token is not active.");
      }
      String user = token.getPrincipal();
      if (user == null)
      {
         throw new VerificationException("Token user was null");
      }
      if (!realm.equals(token.getAudience()))
      {
         throw new VerificationException("Token audience doesn't match domain");

      }
      SkeletonKeyToken.Access access= null;
      if (resource == null) // realm access
      {
         access = token.getRealmAccess();
      }
      else
      {
         access = token.getResourceAccess(resource);
      }
      if (access == null)
      {
         throw new VerificationException("Not authorized for resource access");
      }

      /*

      boolean found = false;
      for (X509Certificate cert : userCerts)
      {
         if (user.equals(cert.getSubjectX500Principal().getName()))
         {
            found = true;
            break;
         }
      }
      if (!found) throw new VerificationException("User: " + user + " was not found in list of client certificates");
      */
      // assuming 1st is root
      String surrogate = null;
      if (access.isVerifyCaller())
      {
         if (caller == null) throw new VerificationException("Caller auth required");
         String certUser = null;
         if (caller instanceof Principal)
         {
            certUser = ((Principal)caller).getName();
         }
         else
         {
            certUser = ((X509Certificate[])caller)[0].getSubjectX500Principal().getName();
         }
         if (!certUser.equals(user))
         {
            // check surrogate
            if (access.hasSurrogate(certUser))
            {
               surrogate = certUser;
            }
            else
            {
               throw new VerificationException("Caller principal does not match token principal");
            }
         }
      }
      SkeletonKeyPrincipal principal = new SkeletonKeyPrincipal(user, surrogate, tokenString, metadata.getClientKeystore(), metadata.getTruststore());
      Set<String> roles = access.getRoles();
      if (roles == null) roles = new HashSet<String>();
      return new SkeletonKeyTokenVerification(token, principal, roles);
   }
}
