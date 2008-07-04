package org.jboss.resteasy.plugins.server.grizzly;

import com.sun.grizzly.tcp.http11.GrizzlyRequest;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlySecurityContext implements SecurityContext
{
   private GrizzlyRequest request;

   public GrizzlySecurityContext(GrizzlyRequest request)
   {
      this.request = request;
   }

   public Principal getUserPrincipal()
   {
      return request.getUserPrincipal();
   }

   public boolean isUserInRole(String role)
   {
      throw new RuntimeException("NOT SUPPORTED");
   }

   public boolean isSecure()
   {
      return request.isSecure();
   }

   public String getAuthenticationScheme()
   {
      return request.getAuthType();
   }
}
