package org.jboss.resteasy.test.providers.custom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.custom.resource.WriterNotBuiltinTestWriter;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterCustomer;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class WriterNotBuiltinTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deployDefaultTestPlain() {
        WebArchive war = TestUtil.prepareArchive(WriterNotBuiltinTest.class.getSimpleName());
        war.addClass(ReaderWriterCustomer.class);
        war.addClass(PortProviderUtil.class);
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.use.builtin.providers", "false");
        return TestUtil.finishContainerPrepare(war, contextParams, WriterNotBuiltinTestWriter.class, ReaderWriterResource.class);
    }

    /**
     * @tpTestDetails A more complete test for RESTEASY-1.
     * TestReaderWriter has no type parameter,
     * so it comes after DefaultPlainText in the built-in ordering.
     * The fact that TestReaderWriter gets called verifies that
     * DefaultPlainText gets passed over.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test1New() throws Exception {
        client = new ResteasyClientBuilder().build();
        Response response = client.target(PortProviderUtil.generateURL("/string", WriterNotBuiltinTest.class.getSimpleName()))
                .request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("text/plain;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        Assert.assertEquals("Response contains wrong content", "hello world", response.readEntity(String.class));
        Assert.assertTrue("Wrong MessageBodyWriter was used", WriterNotBuiltinTestWriter.used);
        client.close();
    }
}
