package org.jboss.resteasy.skeleton.key.jaxrs;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.VerificationException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsBearerTokenFilter implements ContainerRequestFilter
{
   protected ResourceMetadata resourceMetadata;
   private static Logger log = Logger.getLogger(JaxrsBearerTokenFilter.class);

   public JaxrsBearerTokenFilter(ResourceMetadata resourceMetadata)
   {
      this.resourceMetadata = resourceMetadata;
   }

   protected void challengeResponse(ContainerRequestContext request, String error, String description)
   {
      StringBuilder header = new StringBuilder("Bearer realm=\"");
      header.append(resourceMetadata.getRealm()).append("\"");
      if (error != null)
      {
         header.append(", error=\"").append(error).append("\"");
      }
      if (description != null)
      {
         header.append(", error_description=\"").append(description).append("\"");
      }
      request.abortWith(Response.status(401).header("WWW-Authenticate", header.toString()).build());
      return;
   }

   @Context
   protected SecurityContext securityContext;

   @Override
   public void filter(ContainerRequestContext request) throws IOException
   {
      String authHeader = request.getHeaderString("Authorization");
      if (authHeader == null)
      {
         challengeResponse(request, null, null);
         return;
      }

      String[] split = authHeader.trim().split("\\s+");
      if (split == null || split.length != 2) challengeResponse(request, null, null);
      if (!split[0].equalsIgnoreCase("Bearer")) challengeResponse(request, null, null);


      String tokenString = split[1];


      try
      {
         final SkeletonKeyTokenVerification verification = RSATokenVerifier.verify(securityContext.getUserPrincipal(), tokenString, resourceMetadata);
         final boolean isSecure = securityContext.isSecure();
         final String scheme = securityContext.getAuthenticationScheme();
         SecurityContext ctx = new SecurityContext()
         {
            @Override
            public Principal getUserPrincipal()
            {
               return verification.getPrincipal();
            }

            @Override
            public boolean isUserInRole(String role)
            {
               return verification.getRoles().contains(role);
            }

            @Override
            public boolean isSecure()
            {
               return isSecure;
            }

            @Override
            public String getAuthenticationScheme()
            {
               return scheme;
            }
         };
         request.setSecurityContext(ctx);
      }
      catch (VerificationException e)
      {
         log.error("Failed to verify token", e);
         challengeResponse(request, "invalid_token", e.getMessage());
      }
   }

}
