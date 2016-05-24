package org.jboss.resteasy.test.nextgen.wadl.resources.jaxb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */

@Path("/customers")
public class CustomerResource {
    @GET
    @Path("{id}")
    @Produces("application/xml")
    public Customer getCustomer(@PathParam("id") int id) {
        Customer cust = new Customer(id);
        return cust;
    }

}
