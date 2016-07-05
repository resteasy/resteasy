package org.jboss.resteasy.resteasy903;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * RESTEASY-903
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 11, 2014
 */
@Path("test")
public class TestResource
{
   @javax.ws.rs.core.Context javax.servlet.http.HttpServletRequest request;
   @javax.ws.rs.core.Context javax.servlet.http.HttpServletResponse response;
//
   @GET
   @Path("dispatch/static")
   public void dispatchStatic()
   {
      javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/test.html");
System.out.println("request.getContentLength()" + request.getContentLength());
      try
      {
         dispatcher.forward(request, response);
      }
      catch (Exception e )
      {
         throw new RuntimeException(e);
      }

   }
   
   @GET
   @Path("dispatch/dynamic")
   public void dispatchDynamic()
   {
      javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("/forward");

      try
      {
         dispatcher.forward( request, response );
      }
      catch (Exception e )
      {
         throw new RuntimeException( e );
      }
   }
}