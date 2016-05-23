package org.jboss.resteasy.resteasy923;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 26, 2014
 */
@ValidateRequest
public interface SessionResourceParent
{
   @GET
   @Path("resource")
   public String test(@Size(min=4) @QueryParam("param") String param);
}