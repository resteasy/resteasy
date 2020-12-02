package org.jboss.resteasy.test.microprofile.restclient.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

@Path("/theService")
public class QueryParamStyleService {
    @GET
    @Path("get")
    public List<String> getList(@QueryParam("myParam") List<String> srcList) {
        List<String> l = new ArrayList<>();
        l.addAll(srcList);
        l.add("theService reached");
        return l;
    }
}
