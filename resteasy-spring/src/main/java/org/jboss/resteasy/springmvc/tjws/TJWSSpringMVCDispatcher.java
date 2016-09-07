package org.jboss.resteasy.springmvc.tjws;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSRequestPreProcessor;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 */

@Deprecated
public class TJWSSpringMVCDispatcher extends DispatcherServlet
{

   private static final long serialVersionUID = -2514290159304754308L;

   private TJWSRequestPreProcessor requestPreProcessor;

   public TJWSSpringMVCDispatcher()
   {
      this.requestPreProcessor = new TJWSRequestPreProcessor();
   }

   public TJWSSpringMVCDispatcher(SecurityDomain domain)
   {
      this.requestPreProcessor = new TJWSRequestPreProcessor(domain);
   }

   public void setContextPath(String contextPath)
   {
      requestPreProcessor.setContextPath(contextPath);
   }

   public void setSecurityDomain(SecurityDomain domain)
   {
      requestPreProcessor.setSecurityDomain(domain);
   }

   @Override
   protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception
   {
      HttpServletRequest processedRequest = requestPreProcessor.preProcessRequest(request, response);
      if (processedRequest != null)
         super.doService(processedRequest, response);
   }

}
