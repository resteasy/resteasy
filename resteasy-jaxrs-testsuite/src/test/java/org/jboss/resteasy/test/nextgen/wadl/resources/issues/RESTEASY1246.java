package org.jboss.resteasy.test.nextgen.wadl.resources.issues;

import org.jboss.resteasy.test.nextgen.wadl.resources.jaxb.Customer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Created by weli on 6/13/16.
 */
@Path("/issues/1246")
public class RESTEASY1246 {

    @GET
    @Produces({"application/xml", "application/json"})
    public String multipleProvides() {
        return null;
    }
}
