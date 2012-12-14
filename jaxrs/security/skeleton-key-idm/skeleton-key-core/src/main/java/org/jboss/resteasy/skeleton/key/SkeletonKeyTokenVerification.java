package org.jboss.resteasy.skeleton.key;

import org.jboss.resteasy.jose.jws.JWSInput;

import javax.ws.rs.ext.Providers;
import java.security.cert.X509Certificate;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyTokenVerification
{
   protected final SkeletonKeyPrincipal principal;
   protected final Set<String> roles;
   protected final SkeletonKeyToken token;

   public SkeletonKeyTokenVerification(SkeletonKeyToken token, SkeletonKeyPrincipal principal, Set<String> roles)
   {
      this.token = token;
      this.principal = principal;
      this.roles = roles;
   }

   public SkeletonKeyPrincipal getPrincipal()
   {
      return principal;
   }

   public Set<String> getRoles()
   {
      return roles;
   }

   public SkeletonKeyToken getToken()
   {
      return token;
   }
}
