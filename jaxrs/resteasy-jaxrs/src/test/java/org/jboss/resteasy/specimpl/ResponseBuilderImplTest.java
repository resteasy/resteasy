package org.jboss.resteasy.specimpl;

import org.junit.Test;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * @author <a href="dmitry.bedrin@gmail.com">Dmitry Bedrin</a>
 */
public class ResponseBuilderImplTest {

    @Test
    public void testObjectEntityTagValue() throws Exception {
        Response response = Response.ok("entityValue").tag(new EntityTag("etagValue")).build();
        assertEquals("\"etagValue\"", response.getHeaderString("ETag"));
    }

    /**
     * test for RESTEASY-1439: ETag not quoted in ResponseBuilder.tag(String tag)
     */
    @Test
    public void testStringEntityTagValue() throws Exception {
        Response response = Response.ok("entityValue").tag("etagValue").build();
        assertEquals("\"etagValue\"", response.getHeaderString("ETag"));
    }

    @Test
    public void testNotModifiedWithObjectEntityTagValue() throws Exception {
        Response response = Response.notModified(new EntityTag("etagValue")).build();
        assertEquals("\"etagValue\"", response.getHeaderString("ETag"));
    }

    @Test
    public void testNotModifiedWithStringEntityTagValue() throws Exception {
        Response response = Response.notModified("etagValue").build();
        assertEquals("\"etagValue\"", response.getHeaderString("ETag"));
    }

}
