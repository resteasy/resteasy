package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/")
public class CollectionResource {
    private static final String WRONG_REQUEST_ERROR_MSG = "Request contains wrong data";
    @GET
    @Path("array")
    @Produces("application/xml")
    @Wrapped
    public CollectionCustomer[] getCustomers() {
        CollectionCustomer[] custs = {new CollectionCustomer("bill"), new CollectionCustomer("monica")};
        return custs;
    }

    @PUT
    @Path("array")
    @Consumes("application/xml")
    public void putCustomers(@Wrapped CollectionCustomer[] customers) {
        Assert.assertEquals(WRONG_REQUEST_ERROR_MSG, "bill", customers[0].getName());
        Assert.assertEquals(WRONG_REQUEST_ERROR_MSG, "monica", customers[1].getName());
    }

    @GET
    @Path("set")
    @Produces("application/xml")
    @Wrapped
    public Set<CollectionCustomer> getCustomerSet() {
        HashSet<CollectionCustomer> set = new HashSet<CollectionCustomer>();
        set.add(new CollectionCustomer("bill"));
        set.add(new CollectionCustomer("monica"));

        return set;
    }

    @PUT
    @Path("list")
    @Consumes("application/xml")
    public void putCustomers(@Wrapped List<CollectionCustomer> customers) {
        Assert.assertEquals(WRONG_REQUEST_ERROR_MSG, "bill", customers.get(0).getName());
        Assert.assertEquals(WRONG_REQUEST_ERROR_MSG, "monica", customers.get(1).getName());
    }

    @GET
    @Path("list")
    @Produces("application/xml")
    @Wrapped
    public List<CollectionCustomer> getCustomerList() {
        ArrayList<CollectionCustomer> set = new ArrayList<CollectionCustomer>();
        set.add(new CollectionCustomer("bill"));
        set.add(new CollectionCustomer("monica"));

        return set;
    }

    @GET
    @Path("list/response")
    @Produces("application/xml")
    @Wrapped
    public Response getCustomerListResponse() {
        ArrayList<CollectionCustomer> set = new ArrayList<CollectionCustomer>();
        set.add(new CollectionCustomer("bill"));
        set.add(new CollectionCustomer("monica"));
        GenericEntity<List<CollectionCustomer>> genericEntity = new GenericEntity<List<CollectionCustomer>>(set) {
        };
        return Response.ok(genericEntity).build();
    }
}
