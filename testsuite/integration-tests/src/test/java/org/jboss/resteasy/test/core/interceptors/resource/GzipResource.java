package org.jboss.resteasy.test.core.interceptors.resource;


import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Path("/")
public class GzipResource {

    private static Logger logger = Logger.getLogger(GzipResource.class);
    @GET
    @Path("text")
    @Produces("text/plain")
    public Response getText(@Context HttpHeaders headers) {
        String acceptEncoding = headers.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
        logger.info(acceptEncoding);
        Assert.assertEquals("gzip, deflate", acceptEncoding);
        return Response.ok("HELLO WORLD").header("Content-Encoding", "gzip").build();
    }

    @GET
    @Path("encoded/text")
    @GZIP
    public String getGzipText() {
        return "HELLO WORLD";
    }

    @GET
    @Path("bytes")
    @GZIP
    @Produces("text/plain")
    public byte[] getBytes() {
        return "HELLO WORLD".getBytes();
    }

    @GET
    @Path("error")
    @GZIP
    @Produces({"application/json;charset=UTF-8"})
    public StreamingOutput getTest() {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                throw new WebApplicationException(405);
            }
        };
    }

    @GET
    @Path("encoded/text/error")
    @GZIP
    public String getGzipErrorText() {
        throw new WebApplicationException(
                Response.status(500).entity("Hello").type("text/plain").build()
        );
    }

    @PUT
    @Consumes("text/plain")
    @Path("stream")
    public void putStream(InputStream is) throws Exception {
        byte[] bytes = ReadFromStream.readFromStream(1024, is);
        String str = new String(bytes);
        Assert.assertEquals("hello world", str);
    }

    @PUT
    @Consumes("text/plain")
    @Path("text")
    public void putText(String text) throws Exception {
        Assert.assertEquals("hello world", text);
    }
}
