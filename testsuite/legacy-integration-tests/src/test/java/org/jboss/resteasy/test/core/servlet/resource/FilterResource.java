package org.jboss.resteasy.test.core.servlet.resource;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponseWrapper;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test")
public class FilterResource {
    @javax.ws.rs.core.Context
    javax.servlet.http.HttpServletRequest request;
    @javax.ws.rs.core.Context
    javax.servlet.http.HttpServletResponse response;
    @javax.ws.rs.core.Context
    ServletContext context;

    @GET
    @Path("dispatch/static")
    public void dispatchStatic() {
        javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/test.html");

        try {
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("dispatch/dynamic")
    public void dispatchDynamic() {
        javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("/forward");

        try {
            dispatcher.forward(new ServletRequestWrapper(request), new ServletResponseWrapper(response));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}