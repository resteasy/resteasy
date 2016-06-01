package org.jboss.resteasy.plugins.server.tjws;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated See resteasy-undertow module.
 */
@Deprecated
class AuthenticatedHttpServletRequest extends PatchedHttpServletRequest
{
   private SecurityDomain domain;
   private Principal user;
   private String authType;

   public AuthenticatedHttpServletRequest(HttpServletRequest delegate, SecurityDomain domain, Principal user, String authType, String contextPath)
   {
      super(delegate, contextPath);
      this.domain = domain;
      this.user = user;
      this.authType = authType;
   }

   public boolean isUserInRole(String role)
   {
      return domain.isUserInRole(user, role);
   }

   public Principal getUserPrincipal()
   {
      return user;
   }

   @Override
   public String getAuthType()
   {
      return authType;
   }
}
