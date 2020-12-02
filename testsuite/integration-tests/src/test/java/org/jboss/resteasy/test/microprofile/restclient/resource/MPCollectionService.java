package org.jboss.resteasy.test.microprofile.restclient.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class MPCollectionService {
    @GET
    @Path("get")
    public List<String> getList() {
        List<String> l = new ArrayList<>();
        l.add("one");
        l.add("two");
        l.add("three");
        return l;
    }

    @GET
    @Path("ping")
    public String ping() {
        return "pong";
    }

}
