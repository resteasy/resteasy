package org.jboss.resteasy.test.client;

import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 *
 * @author <a href="dmitry.bedrin@gmail.com">Dmitry Bedrin</a>
 */
public class ResponseBuilderImplTest {

    private final String ERROR_MESSAGE = "Incorrect ETag value";

    /**
     * @tpTestDetails Sets entity tag of Response builder
     * @tpPassCrit Response builder contains newly updated value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testObjectEntityTagValue() throws Exception {
        Response response = Response.ok("entityValue").tag(new EntityTag("etagValue")).build();
        Assert.assertEquals(ERROR_MESSAGE, "\"etagValue\"", response.getHeaderString("ETag"));
    }

    /**
     * @tpTestDetails Sets quoted ETag by ResponseBuilder.tag(String tag)
     * @tpPassCrit Response builder contains newly updated value with the quotes
     * @tpInfo RESTEASY-1439
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testStringEntityTagValue() throws Exception {
        Response response = Response.ok("entityValue").tag("etagValue").build();
        Assert.assertEquals(ERROR_MESSAGE, "\"etagValue\"", response.getHeaderString("ETag"));
    }

    /**
     * @tpTestDetails Sets entity tag of Response builder with a not-modified status.
     * @tpPassCrit Response builder contains newly updated value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNotModifiedWithObjectEntityTagValue() throws Exception {
        Response response = Response.notModified(new EntityTag("etagValue")).build();
        Assert.assertEquals(ERROR_MESSAGE, "\"etagValue\"", response.getHeaderString("ETag"));
    }

    /**
     * @tpTestDetails Sets entity tag of Response builder with a not-modified status using ResponseBuilder.tag(String tag)
     * @tpPassCrit Response builder contains newly updated value with the quotes
     * @tpInfo RESTEASY-1439
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testNotModifiedWithStringEntityTagValue() throws Exception {
        Response response = Response.notModified("etagValue").build();
        Assert.assertEquals(ERROR_MESSAGE, "\"etagValue\"", response.getHeaderString("ETag"));
    }

}

