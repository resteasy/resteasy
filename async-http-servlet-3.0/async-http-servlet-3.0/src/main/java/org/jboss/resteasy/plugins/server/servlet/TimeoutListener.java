package org.jboss.resteasy.plugins.server.servlet;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 22, 2012
 */
public class TimeoutListener implements AsyncListener
{
   @Override
   public void onComplete(AsyncEvent event) throws IOException
   {
      // Nothing to do.
   }

   @Override
   public void onTimeout(AsyncEvent event) throws IOException
   {
      ServletResponse response = event.getSuppliedResponse();
      if (response instanceof HttpServletResponse)
      {
         HttpServletResponse httpServletResponse = HttpServletResponse.class.cast(response);
         httpServletResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "timeout");
      }
      event.getAsyncContext().complete();  
   }

   @Override
   public void onError(AsyncEvent event) throws IOException
   {
      event.getAsyncContext().complete();
   }

   @Override
   public void onStartAsync(AsyncEvent event) throws IOException
   {
      // Nothing to do.
   }

}
