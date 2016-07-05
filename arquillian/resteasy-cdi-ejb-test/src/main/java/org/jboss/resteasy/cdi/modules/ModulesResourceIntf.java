package org.jboss.resteasy.cdi.modules;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *  
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 20, 2012
 */
@Local
public interface ModulesResourceIntf
{

   @GET
   @Path("test")
   public abstract Response test();

}