package org.jboss.resteasy.test.core.interceptors.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.Variant;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class GzipResource {

    private static Logger logger = Logger.getLogger(GzipResource.class);

    @GET
    @Path("text")
    @Produces("text/plain")
    public Response getText(@Context HttpHeaders headers) {
        String acceptEncoding = headers.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
        logger.info(acceptEncoding);
        Assertions.assertEquals("gzip, deflate", acceptEncoding);
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
    @Produces({ "application/json;charset=UTF-8" })
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
                Response.status(500).entity("Hello").type("text/plain").build());
    }

    @PUT
    @Consumes("text/plain")
    @Path("stream")
    public void putStream(InputStream is) throws Exception {
        byte[] bytes = ReadFromStream.readFromStream(1024, is);
        String str = new String(bytes);
        Assertions.assertEquals("hello world", str);
    }

    @PUT
    @Consumes("text/plain")
    @Path("text")
    public void putText(String text) throws Exception {
        Assertions.assertEquals("hello world", text);
    }

    @POST
    @Path("/gzippost")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postGZipped(@GZIP Pair pair, @Context HttpHeaders headers) {
        MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
        for (String key : requestHeaders.keySet()) {
            logger.info(key + ": " + requestHeaders.get(key));
        }
        Assertions.assertEquals("gzip", headers.getRequestHeaders().getFirst(HttpHeaders.CONTENT_ENCODING));
        Assertions.assertEquals("first", pair.getP1());
        Assertions.assertEquals("second", pair.getP2());

        return Response.ok().type(MediaType.TEXT_PLAIN).encoding("gzip").build();
    }

    @POST
    @Path("big/send")
    public Response sendBig(byte[] b) {
        return Response.ok().build();
    }

    @POST
    @Path("big/receive")
    public Response receiveBig(int n) {
        Variant variant = new Variant(MediaType.APPLICATION_OCTET_STREAM_TYPE, Locale.ENGLISH, "gzip");
        return Response.ok(new byte[n]).variant(variant).header("xyz", "abc").build();
    }
}
