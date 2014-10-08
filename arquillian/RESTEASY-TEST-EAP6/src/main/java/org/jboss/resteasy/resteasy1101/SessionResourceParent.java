package org.jboss.resteasy.resteasy1101;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.spi.validation.ValidateRequest;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 6, 2014
 */
@ValidateRequest
public interface SessionResourceParent
{
   @GET
   @Path("resource")
   public String test(@Size(min=4) @QueryParam("param") String param);
}