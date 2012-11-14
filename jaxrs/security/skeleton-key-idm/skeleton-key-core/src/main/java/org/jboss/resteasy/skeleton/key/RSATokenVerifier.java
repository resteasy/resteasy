package org.jboss.resteasy.skeleton.key;

import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jwt.JsonSerialization;

import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RSATokenVerifier
{
   public static SkeletonKeyTokenVerification verify(X509Certificate[] userCerts,
                                                     String tokenString, ServiceMetadata metadata) throws VerificationException
   {
      JWSInput input = new JWSInput(tokenString);
      boolean verified = false;
      for (X509Certificate idp : metadata.getIdentityProviderCertificates())
      {
         try
         {
            if (RSAProvider.verify(input, idp.getPublicKey()))
            {
               verified = true;
               break;
            }
         }
         catch (Exception ignore)
         {

         }
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
      if (!metadata.getDomain().equals(token.getAudience()))
      {
         throw new VerificationException("Token audience doesn't match domain");

      }
      SkeletonKeyToken.Access access = token.getServiceToken(metadata.getName());
      if (access == null)
      {
         throw new VerificationException("No service access specified");
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
      String certUser = userCerts[0].getSubjectX500Principal().getName();
      String surrogate = null;
      if (!certUser.equals(user))
      {
         // check surrogate
         if (access.hasSurrogate(certUser))
         {
            surrogate = certUser;
         }
         else
         {
            throw new VerificationException("Certificate principal does not match token principal");
         }
      }
      SkeletonKeyPrincipal principal = new SkeletonKeyPrincipal(user, surrogate, tokenString, metadata.getKeystore(), metadata.getTruststore());
      Set<String> roles = access.getRoles();
      if (roles == null) roles = new HashSet<String>();
      return new SkeletonKeyTokenVerification(principal, roles);
   }
}
