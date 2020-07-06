package org.jboss.resteasy.test.microprofile.restclient.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
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
