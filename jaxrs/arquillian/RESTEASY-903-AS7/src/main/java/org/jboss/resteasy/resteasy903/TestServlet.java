package org.jboss.resteasy.resteasy903;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class TestServlet extends HttpServlet
{
   private static final long serialVersionUID = 1L;

   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException
   {
      System.out.println("entering TestServlet.service()");
      System.out.println("context path: " + req.getServletContext().getContextPath());      RequestDispatcher dispatcher = req.getRequestDispatcher("/forward");
      try
      {
         dispatcher.forward(req, res);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      
   }
}
