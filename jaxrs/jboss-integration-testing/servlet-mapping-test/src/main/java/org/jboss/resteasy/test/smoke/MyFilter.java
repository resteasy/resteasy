package org.jboss.resteasy.test.smoke;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MyFilter implements Filter
{
   public void init(FilterConfig filterConfig) throws ServletException
   {
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
   {
      // test that form parameters still work even if a filter screws up the input stream by calling servletRequest.getParameterMap()
      servletRequest.getParameterMap();
      filterChain.doFilter(servletRequest, servletResponse);
   }

   public void destroy()
   {
   }
}
