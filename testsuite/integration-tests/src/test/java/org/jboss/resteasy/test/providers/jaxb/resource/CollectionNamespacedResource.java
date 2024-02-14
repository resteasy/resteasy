package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.junit.jupiter.api.Assertions;

@Path("/namespaced")
public class CollectionNamespacedResource {
    @GET
    @Path("array")
    @Produces("application/xml")
    @Wrapped
    public CollectionNamespacedCustomer[] getCustomers() {
        CollectionNamespacedCustomer[] custs = { new CollectionNamespacedCustomer("bill"),
                new CollectionNamespacedCustomer("monica") };
        return custs;
    }

    @PUT
    @Path("array")
    @Consumes("application/xml")
    public void putCustomers(@Wrapped CollectionNamespacedCustomer[] customers) {
        Assertions.assertEquals("bill", customers[0].getName());
        Assertions.assertEquals("monica", customers[1].getName());
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
        Assertions.assertEquals("bill", customers.get(0).getName());
        Assertions.assertEquals("monica", customers.get(1).getName());
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
        GenericEntity<List<CollectionNamespacedCustomer>> genericEntity = new GenericEntity<List<CollectionNamespacedCustomer>>(
                set) {
        };
        return Response.ok(genericEntity).build();
    }
}
