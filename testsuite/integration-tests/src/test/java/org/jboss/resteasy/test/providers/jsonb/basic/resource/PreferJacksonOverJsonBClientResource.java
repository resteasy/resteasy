package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class PreferJacksonOverJsonBClientResource {

    private static List<String> list = new ArrayList<>();
    static {
        list.add("a");
        list.add("b");
    }

    @GET
    @Path("core")
    @Produces(MediaType.APPLICATION_JSON)
    public List core() {
        return list;
    }

    @GET
    @Path("call")
    public void call(@HeaderParam("clientURL") String clientURL) throws Exception {
        // query
        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget base = client.target(clientURL);
        Response response = base.request().get();

        if (response.getStatus() != HttpResponseCodes.SC_OK) {
            throw new Exception("Client in deployment received wrong response code");
        }
    }

}
