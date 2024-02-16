package org.jboss.resteasy.test.providers.plain.resource;

import java.nio.charset.Charset;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class CharsetResource {

    private static Logger logger = Logger.getLogger(CharsetResource.class);

    @POST
    @Path("produces/string/utf16")
    @Consumes("text/plain")
    @Produces("text/plain;charset=UTF-16")
    public String stringProducesUtf16(String s) {
        logger.info("server default charset: " + Charset.defaultCharset());
        logger.info("s: " + s);
        return s;
    }

    @POST
    @Path("accepts/string/default")
    @Consumes("text/plain")
    public String stringAcceptsDefault(String s) {
        logger.info("s: " + s);
        return s;
    }

    @POST
    @Path("produces/foo/utf16")
    @Consumes("text/plain")
    @Produces("text/plain;charset=UTF-16")
    public CharsetFoo fooProducesUtf16(CharsetFoo foo) {
        logger.info("foo: " + foo.valueOf());
        return foo;
    }

    @POST
    @Path("accepts/foo/default")
    @Consumes("text/plain")
    @Produces("text/plain")
    public CharsetFoo fooAcceptsDefault(CharsetFoo foo) {
        logger.info("foo: " + foo.valueOf());
        return foo;
    }

    @POST
    @Path("accepts/form/default")
    @Produces("application/x-www-form-urlencoded")
    @Consumes("application/x-www-form-urlencoded")
    @Encoded
    public MultivaluedMap<String, String> formAcceptsDefault(MultivaluedMap<String, String> form) {
        Assertions.assertTrue(form.containsKey("title"), "Form doesn't contain the expected key");
        String s = form.getFirst("title");
        logger.info("s: " + s);
        return form;
    }
}
