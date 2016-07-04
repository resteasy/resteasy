package org.jboss.resteasy.resteasy903;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class ForwardServlet extends HttpServlet
{
   private static final long serialVersionUID = 1L;
   
   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException
   {
      System.out.println("enterng ForwardServlet.service()");
      res.getOutputStream().write("hello".getBytes());
   }
}
