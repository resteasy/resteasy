package org.jboss.resteasy.test.providers.jettison.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/user")
@Produces({"application/xml", "application/json"})
@Consumes({"application/xml", "application/json"})
public interface UserEntityWebservice extends CRUDEntityWebservice {

}
