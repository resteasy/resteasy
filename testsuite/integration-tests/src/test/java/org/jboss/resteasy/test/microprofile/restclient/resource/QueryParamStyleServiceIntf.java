package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

@RegisterRestClient(baseUri ="http://localhost:8080/queryParamStyle_service", configKey="qParamS")
@Path("/theService")
@Singleton
public interface QueryParamStyleServiceIntf {
    @GET
    //@POST
    @Path("get")
    List<String> getList(@QueryParam("myParam") List<String> l);
}
