package org.jboss.resteasy.springmvc;

import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.ResponseInvoker;
import org.jboss.resteasy.spi.HttpResponse;

public class ResteasyResponseWrapper
{

   private HttpResponse response;
   private HttpServletResponse servletResponse;
   private ResponseInvoker responseInvoker;

   public HttpResponse getResponse()
   {
      return response;
   }

   public void setResponse(HttpResponse response)
   {
      this.response = response;
   }

   public HttpServletResponse getServletResponse()
   {
      return servletResponse;
   }

   public void setServletResponse(HttpServletResponse servletResponse)
   {
      this.servletResponse = servletResponse;
   }

   public ResponseInvoker getResponseInvoker()
   {
      return responseInvoker;
   }

   public void setResponseInvoker(ResponseInvoker responseInvoker)
   {
      this.responseInvoker = responseInvoker;
   }

}
