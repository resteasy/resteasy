package org.jboss.resteasy.test.core.basic.resource;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

public class MyFilter implements Filter {
   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      // test that form parameters still work even if a filter cause error in the input stream by calling servletRequest.getParameterMap()
      servletRequest.getParameterMap();
      filterChain.doFilter(servletRequest, servletResponse);
   }

   public void destroy() {
   }
}
