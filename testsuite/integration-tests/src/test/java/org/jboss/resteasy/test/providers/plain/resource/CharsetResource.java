package org.jboss.resteasy.test.providers.plain.resource;

import org.jboss.logging.Logger;
import org.junit.Assert;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.Encoded;
import javax.ws.rs.core.MultivaluedMap;
import java.nio.charset.Charset;

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
         Assert.assertTrue("Form doesn't contain the expected key", form.containsKey("title"));
         String s = form.getFirst("title");
         logger.info("s: " + s);
         return form;
     }
}
