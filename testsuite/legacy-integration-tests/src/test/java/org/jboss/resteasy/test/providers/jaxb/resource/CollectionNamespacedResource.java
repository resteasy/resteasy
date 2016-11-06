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

@Path("/namespaced")
public class CollectionNamespacedResource {
    @GET
    @Path("array")
    @Produces("application/xml")
    @Wrapped
    public CollectionNamespacedCustomer[] getCustomers() {
        CollectionNamespacedCustomer[] custs = {new CollectionNamespacedCustomer("bill"), new CollectionNamespacedCustomer("monica")};
        return custs;
    }

    @PUT
    @Path("array")
    @Consumes("application/xml")
    public void putCustomers(@Wrapped CollectionNamespacedCustomer[] customers) {
        Assert.assertEquals("bill", customers[0].getName());
        Assert.assertEquals("monica", customers[1].getName());
    }

    @GET
    @Path("set")
    @Produces("application/xml")
    @Wrapped
    public Set<CollectionNamespacedCustomer> getCustomerSet() {
        HashSet<CollectionNamespacedCustomer> set = new HashSet<CollectionNamespacedCustomer>();
        set.add(new CollectionNamespacedCustomer("bill"));
        set.add(new CollectionNamespacedCustomer("monica"));

        return set;
    }

    @PUT
    @Path("list")
    @Consumes("application/xml")
    public void putCustomers(@Wrapped List<CollectionNamespacedCustomer> customers) {
        Assert.assertEquals("bill", customers.get(0).getName());
        Assert.assertEquals("monica", customers.get(1).getName());
    }

    @GET
    @Path("list")
    @Produces("application/xml")
    @Wrapped
    public List<CollectionNamespacedCustomer> getCustomerList() {
        ArrayList<CollectionNamespacedCustomer> set = new ArrayList<CollectionNamespacedCustomer>();
        set.add(new CollectionNamespacedCustomer("bill"));
        set.add(new CollectionNamespacedCustomer("monica"));

        return set;
    }

    @GET
    @Path("list/response")
    @Produces("application/xml")
    @Wrapped
    public Response getCustomerListResponse() {
        ArrayList<CollectionNamespacedCustomer> set = new ArrayList<CollectionNamespacedCustomer>();
        set.add(new CollectionNamespacedCustomer("bill"));
        set.add(new CollectionNamespacedCustomer("monica"));
        GenericEntity<List<CollectionNamespacedCustomer>> genericEntity = new GenericEntity<List<CollectionNamespacedCustomer>>(set) {
        };
        return Response.ok(genericEntity).build();
    }
}
