package org.resteasy.plugins.server.grizzly;

import com.sun.grizzly.http.SelectorThread;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyHttpServletServer extends GrizzlyServer
{
   protected Servlet servlet;
   protected String path;


   public Servlet getServlet()
   {
      return servlet;
   }

   public void setServlet(Servlet servlet)
   {
      this.servlet = servlet;
   }

   public void setServletPath(String path)
   {
      this.path = path;
   }

   public String getServletPath()
   {
      return this.path;
   }

   protected void initSelectorThread()
           throws ServletException
   {
      selectorThread = new SelectorThread();
      selectorThread.setPort(port);
      SingletonServletAdapter servletAdapter = new SingletonServletAdapter(path);
      servletAdapter.setServletInstance(servlet);
      servletAdapter.init();
      selectorThread.setAdapter(
              servletAdapter);
   }


}
