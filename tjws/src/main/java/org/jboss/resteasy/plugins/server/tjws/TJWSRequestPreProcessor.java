package org.jboss.resteasy.plugins.server.tjws;

import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * @deprecated See resteasy-undertow module.
 */
@Deprecated
public class TJWSRequestPreProcessor
{
   private SecurityDomain domain;
   private String contextPath = "";

   public TJWSRequestPreProcessor(SecurityDomain domain)
   {
      this.domain = domain;
   }

   public TJWSRequestPreProcessor()
   {
   }

   public void setContextPath(String contextPath)
   {
      if (contextPath == null) contextPath = "";
      else if (contextPath.equals("/")) contextPath = "";
      this.contextPath = contextPath;
   }

   public void setSecurityDomain(SecurityDomain domain)
   {
      this.domain = domain;
   }


   public HttpServletRequest preProcessRequest(HttpServletRequest request, HttpServletResponse response)
           throws IOException
   {
      if (domain != null)
      {
         String auth = request.getHeader(HttpHeaderNames.AUTHORIZATION);
         if (auth != null && auth.length() > 5)
         {
            String type = auth.substring(0, 5);
            type = type.toLowerCase();
            if ("basic".equals(type))
            {
               String cookie = auth.substring(6);
               cookie = new String(Base64.decodeBase64(cookie.getBytes()));
               String[] split = cookie.split(":");
               //System.out.println("Authenticating user: " + split[0] + " passwd: " + split[1]);
               Principal user = null;
               try
               {
                  user = domain.authenticate(split[0], split[1]);
               }
               catch (SecurityException e)
               {
                  response.sendError(HttpResponseCodes.SC_UNAUTHORIZED);
                  return null;
               }
               request = new AuthenticatedHttpServletRequest(request, domain, user, "BASIC", contextPath);
            }
         }
      }
      else
      {
         // fix bug in non-encoded getRequestURI and URL
         request = new PatchedHttpServletRequest(request, contextPath);
      }
      return request;
   }


}
