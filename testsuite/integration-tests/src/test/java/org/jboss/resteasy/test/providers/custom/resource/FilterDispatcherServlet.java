package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.logging.Logger;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;

public class FilterDispatcherServlet extends HttpServlet {
   private static Logger logger = Logger.getLogger(FilterDispatcherServlet.class);

   private static final long serialVersionUID = 1L;

   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
      logger.info("entering TestServlet.service()");
      logger.info("context path: " + req.getServletContext().getContextPath());
      RequestDispatcher dispatcher = req.getRequestDispatcher("/forward");
      try {
         dispatcher.forward(req, res);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
