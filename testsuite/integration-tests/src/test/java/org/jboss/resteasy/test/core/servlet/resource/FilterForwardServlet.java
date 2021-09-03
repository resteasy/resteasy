package org.jboss.resteasy.test.core.servlet.resource;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;

public class FilterForwardServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
      res.getOutputStream().write("forward".getBytes());
   }
}
