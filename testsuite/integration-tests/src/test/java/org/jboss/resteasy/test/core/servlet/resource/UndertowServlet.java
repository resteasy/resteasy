package org.jboss.resteasy.test.core.servlet.resource;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;

public class UndertowServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
      RequestDispatcher dispatcher = req.getRequestDispatcher("/forward");
      try {
         dispatcher.forward(req, res);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
