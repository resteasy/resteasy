package org.jboss.resteasy.resteasy1008;

import javax.ejb.Local;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 26, 2014
 */
@Local
//@Path("test")
//@ValidateRequest
public interface SessionResource
{
   @GET
   @Path("resource/{param}")
   public int test(@Min(7) @PathParam("param") int param);
}