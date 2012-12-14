package org.jboss.resteasy.skeleton.key.jaxrs;

import org.jboss.resteasy.skeleton.key.RealmConfiguration;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.BindingPriority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@BindingPriority(BindingPriority.AUTHENTICATION)
public class JaxrsOAuthLoginFilter implements ContainerRequestFilter, ContainerResponseFilter
{
   protected RealmConfiguration realmInfo;

   public JaxrsOAuthLoginFilter(RealmConfiguration realmInfo)
   {
      this.realmInfo = realmInfo;
   }

   @Context
   protected SecurityContext securityContext;

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      JaxrsOAuthLogin login = new JaxrsOAuthLogin(realmInfo, requestContext, securityContext);
      if (!login.login())
      {
         return;
      }
      requestContext.setProperty(JaxrsOAuthLoginFilter.class.getName(), login.getCookies());
      final SkeletonKeyTokenVerification verification = login.getVerification();
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
      requestContext.setSecurityContext(ctx);

   }

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
   {
      List<NewCookie> cookies = (List<NewCookie>)requestContext.getProperty(JaxrsOAuthLoginFilter.class.getName());
      if (cookies != null)
      {
         for (NewCookie cookie : cookies) responseContext.getHeaders().add(HttpHeaderNames.SET_COOKIE, cookie);
      }
   }
}
