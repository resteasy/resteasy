package org.jboss.resteasy.cdi.generic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 16, 2012
 */
public interface ConcreteResourceIntf
{
   @GET
   @Path("injection")
   public abstract Response testGenerics();

   @GET
   @Path("decorators/clear")
   public abstract Response clear();

   @GET
   @Path("decorators/execute")
   public abstract Response execute();
   
   @GET
   @Path("decorators/test")
   public abstract Response testDecorators();
}