package org.jboss.resteasy.test.client.proxy.resource.GenericEntities;

import java.util.HashMap;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
public interface MultipleGenericEntities<K, V> {

    @GET
    @Path("hashMap")
    @Produces(MediaType.APPLICATION_JSON)
    HashMap<K, V> findHashMap();

}
