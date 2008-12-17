package org.jboss.resteasy.springmvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.plugins.server.servlet.HttpServletInputMessage;
import org.jboss.resteasy.plugins.server.servlet.ServletUtil;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 */
public class ResteasyRequestWrapper
{

   private HttpRequest httpRequest = null;
   private HttpServletRequest httpServletRequest;
   private ResourceInvoker invoker;
   private Integer errorCode = null;
   private String errorMessage;

   public ResteasyRequestWrapper(HttpServletRequest request) throws ServletException, IOException
   {
      this(request, request.getMethod(), "");
   }

   public ResteasyRequestWrapper(HttpServletRequest request, String httpMethod, String prefix)
           throws ServletException, IOException
   {
      this.httpServletRequest = request;
      HttpHeaders headers = ServletUtil.extractHttpHeaders(request);
      UriInfo uriInfo = ServletUtil.extractUriInfo(request, prefix);
      // TODO: how are we supposed to get the response to create the
      // wrapper!!!!? The null response will only make it so that the
      // Asynchronous invocations won't work

      // HttpServletResponseWrapper theResponse = new
      // HttpServletResponseWrapper(httpServletResponse, dispatcher
      // .getProviderFactory());
      // TODO: the two nulls are the response and the dispatcher. Those
      // are not needed here... They are only needed for asynchronous
      // invocations. Suggest Core RESTEasy refactoring to change the
      // async invocation
      httpRequest = new HttpServletInputMessage(request, null, headers, uriInfo, httpMethod
              .toUpperCase(), null);
   }

   public HttpServletRequest getHttpServletRequest()
   {
      return httpServletRequest;
   }

   public HttpRequest getHttpRequest()
   {
      return httpRequest;
   }

   public ResourceInvoker getInvoker()
   {
      return invoker;
   }

   public void setInvoker(ResourceInvoker invoker)
   {
      this.invoker = invoker;
   }

   public Integer getErrorCode()
   {
      return errorCode;
   }

   public void setErrorCode(Integer errorCode)
   {
      this.errorCode = errorCode;
   }

   public String getErrorMessage()
   {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage)
   {
      this.errorMessage = errorMessage;
   }

   public void setError(Integer errorCode, String errorMessage)
   {
      setErrorCode(errorCode);
      setErrorMessage(errorMessage);
   }
   
   
}
