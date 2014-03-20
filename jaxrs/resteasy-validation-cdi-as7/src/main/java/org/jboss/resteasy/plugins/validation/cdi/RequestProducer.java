package org.jboss.resteasy.plugins.validation.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import org.jboss.resteasy.logging.Logger;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 13, 2014
 */
@WebListener
@ApplicationScoped
public class RequestProducer implements ServletRequestListener
{
   private final Logger log = Logger.getLogger(RequestProducer.class);
   private static ThreadLocal<ServletRequest> SERVLET_REQUESTS = new ThreadLocal<ServletRequest>();
   
   @Override
   public void requestInitialized(ServletRequestEvent sre)
   {
      SERVLET_REQUESTS.set(sre.getServletRequest());
   }

   @Override
   public void requestDestroyed(ServletRequestEvent sre)
   {
      SERVLET_REQUESTS.remove();
   }

   @Produces
   @ResteasyValidationCDIProducerBinding
   private HttpServletRequest getRequest()
   {
      ServletRequest request = SERVLET_REQUESTS.get();
      if (request instanceof HttpServletRequest)
      {
         return HttpServletRequest.class.cast(SERVLET_REQUESTS.get());
      }
      log.warn("RequestProducer: Didn't get a HttpServletRequest. Unable to access request headers");
      return null;
   }
}