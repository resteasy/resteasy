package org.jboss.resteasy.test.core.servlet.resource;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.ServletResponseWrapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("test")
public class FilterResource {
   @jakarta.ws.rs.core.Context
   jakarta.servlet.http.HttpServletRequest request;
   @jakarta.ws.rs.core.Context
   jakarta.servlet.http.HttpServletResponse response;
   @jakarta.ws.rs.core.Context
   ServletContext context;

   @GET
   @Path("dispatch/static")
   public void dispatchStatic() {
      jakarta.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/test.html");

      try {
         dispatcher.forward(request, response);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @GET
   @Path("dispatch/dynamic")
   public void dispatchDynamic() {
      jakarta.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("/forward");

      try {
         dispatcher.forward(new ServletRequestWrapper(request), new ServletResponseWrapper(response));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
