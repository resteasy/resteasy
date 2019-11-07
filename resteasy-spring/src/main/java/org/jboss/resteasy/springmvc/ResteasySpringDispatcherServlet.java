package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.ResteasyContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ResteasySpringDispatcherServlet extends DispatcherServlet {
   @Override
   protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
      try {
         ServletContext servletContext = this.getServletContext();
         Map<Class<?>, Object> map = ResteasyContext.getContextDataMap();
         map.put(ServletContext.class, servletContext);
         super.doDispatch(request, response);
      } finally {
         ResteasyContext.popContextData(ServletContext.class);
      }
   }
}
