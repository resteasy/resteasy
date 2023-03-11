package org.jboss.resteasy.embedded.test.providers.custom;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.se.ConfigurationOption;
import org.jboss.resteasy.embedded.test.AbstractBootstrapTest;
import org.jboss.resteasy.embedded.test.TestApplication;
import org.jboss.resteasy.embedded.test.providers.custom.resource.ReaderWriterResource;
import org.jboss.resteasy.embedded.test.providers.custom.resource.WriterNotBuiltinTestWriter;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Demonstrate MessageBodyWriter, MessageBodyReader
 * @tpSince RESTEasy 4.1.0
 */
public class WriterNotBuiltinTest extends AbstractBootstrapTest {

    @Before
    public void setup() throws Exception {
        start(new TestApplication(ReaderWriterResource.class, WriterNotBuiltinTestWriter.class),
                SeBootstrap.Configuration.builder()
                        .property(ConfigurationOption.REGISTER_BUILT_INS.key(), false)
                        .build());
    }

    /**
     * @tpTestDetails TestReaderWriter has no type parameter,
     *                so it comes after DefaultPlainText in the built-in ordering.
     *                The fact that TestReaderWriter gets called verifies that
     *                DefaultPlainText gets passed over.
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    public void test1New() throws Exception {
        Response response = client.target(generateURL("/string")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("text/plain;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        Assert.assertEquals("Response contains wrong content", "hello world", response.readEntity(String.class));
        Assert.assertTrue("Wrong MessageBodyWriter was used", WriterNotBuiltinTestWriter.used);
    }
}
