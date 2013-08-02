package org.resteasy.test.ejb;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public interface LocatingResource
{
   @Path("locating")
   SimpleResource getLocating();
}
