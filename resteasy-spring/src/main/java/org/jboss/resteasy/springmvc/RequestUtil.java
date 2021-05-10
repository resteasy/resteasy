package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.spi.HttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class RequestUtil
{

   private static String RESPONSE_WRAPPER_KEY = RequestUtil.class.getName() + ".RESPONSE_WRAPPER";

   public static HttpServletRequest getRequest()
   {
      return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
   }

   public static ResteasyRequestWrapper getRequestWrapper(HttpServletRequest request) throws ServletException,
         IOException
   {
      ResteasyRequestWrapper wrapper = (ResteasyRequestWrapper) request.getAttribute(RESPONSE_WRAPPER_KEY);
      if (wrapper == null)
      {
         request.setAttribute(RESPONSE_WRAPPER_KEY, wrapper = new ResteasyRequestWrapper(request));
      }
      return wrapper;
   }

   public static HttpRequest getHttpRequest(HttpServletRequest request) throws ServletException, IOException
   {
      return getRequestWrapper(request).getHttpRequest();
   }

   public static ResteasyRequestWrapper getRequestWrapper(HttpServletRequest request, String method, String prefix)
         throws ServletException, IOException
   {
      ResteasyRequestWrapper wrapper = (ResteasyRequestWrapper) request.getAttribute(RESPONSE_WRAPPER_KEY);
      if (wrapper == null)
      {
         request.setAttribute(RESPONSE_WRAPPER_KEY, wrapper = new ResteasyRequestWrapper(request, method, prefix));
      }
      return wrapper;
   }
}
