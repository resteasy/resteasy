package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value = "/GetTest")
public class MethodDefaultResource {

    static String html_content =
            "<html>" + "<head><title>CTS-get text/html</title></head>" +
                    "<body>CTS-get text/html</body></html>";

    @GET
    public Response getPlain() {
        return Response.ok("CTS-get text/plain").header("CTS-HEAD", "text-plain").
                build();
    }

    @GET
    @Produces(value = "text/html")
    public Response getHtml() {
        return Response.ok(html_content).header("CTS-HEAD", "text-html").
                build();
    }

    @GET
    @Path(value = "/sub")
    public Response getSub() {
        return Response.ok("CTS-get text/plain").header("CTS-HEAD",
                "sub-text-plain").
                build();
    }

    @GET
    @Path(value = "/sub")
    @Produces(value = "text/html")
    public Response headSub() {
        return Response.ok(html_content).header("CTS-HEAD", "sub-text-html").
                build();
    }
}
