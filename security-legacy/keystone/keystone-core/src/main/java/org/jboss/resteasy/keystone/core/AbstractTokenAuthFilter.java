package org.jboss.resteasy.keystone.core;

import org.jboss.resteasy.keystone.core.i18n.LogMessages;
import org.jboss.resteasy.keystone.core.i18n.Messages;
import org.jboss.resteasy.keystone.model.Access;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Providers;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractTokenAuthFilter implements ContainerRequestFilter
{
   protected X509Certificate certificate;

   protected AbstractTokenAuthFilter(X509Certificate certificate)
   {
      this.certificate = certificate;
   }

   @Context
   SecurityContext securityContext;

   @Context
   Providers providers;
   @SuppressWarnings(value = "unchecked")
   protected Access signed(String header)
   {
      PKCS7SignatureInput input = null;
      boolean verify = false;
      try
      {
         input = new PKCS7SignatureInput(header);
         input.setProviders(providers);
         verify = input.verify(certificate);
      }
      catch (Exception e)
      {
         throw new WebApplicationException(403);
      }
      if (!verify) throw new WebApplicationException(403);
      try
      {
         return (Access)input.getEntity(Access.class, MediaType.APPLICATION_JSON_TYPE);
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.error(Messages.MESSAGES.failedToUnmarshall(), e);
         throw new WebApplicationException(403);
      }
   }

   protected abstract Access getTokenFromServer(String header);

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      String xAuthToken = requestContext.getHeaderString("X-Auth-Token");
      String xAuthSignedToken = requestContext.getHeaderString("X-Auth-Signed-Token");
      Access token = null;
      if (xAuthToken == null && xAuthSignedToken == null) return;
      else if (xAuthSignedToken != null && certificate != null)
      {
         token = signed(xAuthSignedToken);
      }
      else if (xAuthToken != null)
      {
         token = getTokenFromServer(xAuthToken);
      }
      if (token == null) return; // do nothing
      if (token.getToken().expired()) return; // todo maybe throw 401 with an error stating token is expired?

      final UserPrincipal principal = new UserPrincipal(token.getUser());
      final Set<String> roleSet = new HashSet<String>();
      for (Role role : token.getUser().getRoles())
      {
         roleSet.add(role.getName());
      }
      SecurityContext ctx = new SecurityContext()
      {
         @Override
         public Principal getUserPrincipal()
         {
            return principal;
         }

         @Override
         public boolean isUserInRole(String role)
         {
            return roleSet.contains(role);
         }

         @Override
         public boolean isSecure()
         {
            return securityContext.isSecure();
         }

         @Override
         public String getAuthenticationScheme()
         {
            return securityContext.getAuthenticationScheme();
         }
      };
      requestContext.setSecurityContext(ctx);
   }
}
