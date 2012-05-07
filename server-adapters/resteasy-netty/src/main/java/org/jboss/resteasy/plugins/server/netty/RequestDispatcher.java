package org.jboss.resteasy.plugins.server.netty;

import org.apache.commons.codec.binary.Base64;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;

/**
 * Helper/delegate class to unify Servlet and Filter dispatcher implementations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RequestDispatcher
{
   private final static Logger logger = Logger.getLogger(RequestDispatcher.class);

   protected SynchronousDispatcher dispatcher;
   protected ResteasyProviderFactory providerFactory;
   protected String servletMappingPrefix = "";
   protected SecurityDomain domain;

   public RequestDispatcher()
   {
   }

   public SynchronousDispatcher getDispatcher()
   {
      return dispatcher;
   }

   public SecurityDomain getDomain()
   {
      return domain;
   }

   public void setDomain(SecurityDomain domain)
   {
      this.domain = domain;
   }

   public void setDispatcher(SynchronousDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public String getServletMappingPrefix()
   {
      return servletMappingPrefix;
   }

   public void setServletMappingPrefix(String servletMappingPrefix)
   {
      this.servletMappingPrefix = servletMappingPrefix;
   }

   public void service(String protocol, org.jboss.netty.handler.codec.http.HttpRequest request, HttpResponse response, boolean handleNotFound) throws Exception
   {
      try
      {
         //logger.info("***PATH: " + request.getRequestURL());
         // classloader/deployment aware RestasyProviderFactory.  Used to have request specific
         // ResteasyProviderFactory.getInstance()
         ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
         if (defaultInstance instanceof ThreadLocalResteasyProviderFactory)
         {
            ThreadLocalResteasyProviderFactory.push(providerFactory);
         }

         SecurityContext securityContext = new NettySecurityContext();
         if (domain != null)
         {
            securityContext = basicAuthentication(request, response);
            if (securityContext == null) // not authenticated
            {
               return;
            }
         }

         HttpHeaders headers = null;
         UriInfoImpl uriInfo = null;
         try
         {
            headers = NettyUtil.extractHttpHeaders(request);
            uriInfo = NettyUtil.extractUriInfo(request, servletMappingPrefix, protocol);
         }
         catch (Exception e)
         {
            response.sendError(400);
            // made it warn so that people can filter this.
            logger.warn("Failed to parse request.", e);
            return;
         }

         HttpRequest in = new NettyHttpRequest(headers, uriInfo, request.getMethod().getName(), dispatcher, response);
         ChannelBufferInputStream is = new ChannelBufferInputStream(request.getContent());
         in.setInputStream(is);

         try
         {
            ResteasyProviderFactory.pushContext(SecurityContext.class, securityContext);
            if (handleNotFound)
            {
               dispatcher.invoke(in, response);
            }
            else
            {
               dispatcher.invokePropagateNotFound(in, response);
            }
         }
         finally
         {
            ResteasyProviderFactory.clearContextData();
         }
      }
      finally
      {
         ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
         if (defaultInstance instanceof ThreadLocalResteasyProviderFactory)
         {
            ThreadLocalResteasyProviderFactory.pop();
         }

      }
   }

   private SecurityContext basicAuthentication(org.jboss.netty.handler.codec.http.HttpRequest request, HttpResponse response) throws IOException
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
               return new NettySecurityContext(user, domain, "BASIC", true);
            }
            catch (SecurityException e)
            {
               response.sendError(HttpResponseCodes.SC_UNAUTHORIZED);
               return null;
            }
         }
         else
         {
            response.sendError(HttpResponseCodes.SC_UNAUTHORIZED);
            return null;
         }
      }
      return null;
   }


}