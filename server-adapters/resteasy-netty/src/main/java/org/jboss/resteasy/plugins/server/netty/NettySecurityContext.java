package org.jboss.resteasy.plugins.server.netty;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NettySecurityContext implements SecurityContext
{
   protected Principal principal;
   protected SecurityDomain domain;
   protected String authScheme;
   protected boolean isSecure = false;

   public NettySecurityContext()
   {
   }

   public NettySecurityContext(Principal principal, SecurityDomain domain, String authScheme, boolean secure)
   {
      this.principal = principal;
      this.domain = domain;
      this.authScheme = authScheme;
      isSecure = secure;
   }

   @Override
   public Principal getUserPrincipal()
   {
      return principal;
   }

   @Override
   public boolean isUserInRole(String role)
   {
      if (domain == null) return false;
      return domain.isUserInRoll(principal, role);
   }

   @Override
   public boolean isSecure()
   {
      return isSecure;
   }

   @Override
   public String getAuthenticationScheme()
   {
      return authScheme;
   }

   public void setPrincipal(Principal principal)
   {
      this.principal = principal;
   }

   public void setDomain(SecurityDomain domain)
   {
      this.domain = domain;
   }

   public void setAuthScheme(String authScheme)
   {
      this.authScheme = authScheme;
   }

   public void setSecure(boolean secure)
   {
      isSecure = secure;
   }
}
