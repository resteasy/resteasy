package org.jboss.resteasy.plugins.server.tjws;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated See resteasy-undertow module.
 */
@Deprecated
public class TJWSServletDispatcher extends HttpServletDispatcher
{
   private TJWSRequestPreProcessor requestPreProcessor;

   public TJWSServletDispatcher()
   {
      this.requestPreProcessor = new TJWSRequestPreProcessor();
   }

   public TJWSServletDispatcher(SecurityDomain domain)
   {
      this.requestPreProcessor = new TJWSRequestPreProcessor(domain);
   }

   public void setContextPath(String contextPath)
   {
      requestPreProcessor.setContextPath(contextPath);
   }

   @Override
   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      HttpServletRequest processedRequest = requestPreProcessor.preProcessRequest(request, response);
      if (processedRequest != null)
         super.service(httpMethod, processedRequest, response);
   }

   public void setSecurityDomain(SecurityDomain domain)
   {
      requestPreProcessor.setSecurityDomain(domain);
   }

   @Override
   public ServletConfig getServletConfig()
   {
      return null;  // it will never be initialized
   }

   @Override
   public ServletContext getServletContext()
   {
      return null;  // it will never be initialized
   }
}
