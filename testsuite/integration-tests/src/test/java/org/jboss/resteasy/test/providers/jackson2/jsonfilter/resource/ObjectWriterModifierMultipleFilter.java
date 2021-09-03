package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyObjectWriterInjector;

public class ObjectWriterModifierMultipleFilter implements Filter {
   private static ObjectFilterModifierMultiple modifier = new ObjectFilterModifierMultiple();

   @Override
   public void init(FilterConfig filterConfig) throws ServletException {
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
      ResteasyObjectWriterInjector.set(Thread.currentThread().getContextClassLoader(), modifier);
      chain.doFilter(request, response);
   }

   @Override
   public void destroy() {
   }
}
