package org.jboss.resteasy.test.nextgen.wadl.resources;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/basic")
public class BasicResource {

    private String name;

    @GET
    public String get(@PathParam("name") String name) {
        return "Hello, " + name;
    }

    @POST
    public void post(@PathParam("name2") String name2) {
        this.name = name2;
    }

    @GET
    @Path("composite/{pathParam}")
    @Produces("text/plain")
    public String composite(@PathParam("pathParam") String pathParam,
                            @HeaderParam("headerParam") String headerParam,
                            @QueryParam("queryParam") String queryParam,
                            @MatrixParam("matrixParam") String matrixParam,
                            @CookieParam("cookieParam") String cookieParam) {
        return "p:P;h:H;q:Q;m:M;c:C"
                .replaceAll("P", pathParam)
                .replaceAll("H", headerParam)
                .replaceAll("Q", queryParam)
                .replaceAll("M", matrixParam)
                .replaceAll("C", cookieParam);
    }

}
