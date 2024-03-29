package org.jboss.resteasy.test.providers.custom;

import java.net.HttpURLConnection;
import java.net.URL;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterClient;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterCurlyBraces;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterCustomer;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterCustomerWriter;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterHignPriorityCustomerWriter;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterLowPriorityCustomerWriter;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterNowhereClient;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterResource;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterSpaces;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterSub;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEasy issues
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ReaderWriterTest {
    static ResteasyClient client;
    static final String PriorityDeploymenetName = "ReaderWriterCustomerWriterWithPriority";

    @Deployment(name = "ReaderWriterCustomerWriter")
    public static Archive<?> deployCustomWriter() {
        WebArchive war = TestUtil.prepareArchive(ReaderWriterCustomerWriter.class.getSimpleName());
        war.addClass(ReaderWriterCustomer.class);
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ReaderWriterCustomerWriter.class, ReaderWriterResource.class);
    }

    @Deployment(name = "ReaderWriterResource")
    public static Archive<?> deployReaderWriterClass() {
        WebArchive war = TestUtil.prepareArchive(ReaderWriterResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ReaderWriterResource.class);
    }

    @Deployment(name = "ReaderWriterSpaces")
    public static Archive<?> deployReaderWriterSpaces() {
        WebArchive war = TestUtil.prepareArchive(ReaderWriterSpaces.class.getSimpleName());
        war.addClass(ReaderWriterSub.class);
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ReaderWriterSpaces.class);
    }

    @Deployment(name = "ReaderWriterCurlyBraces")
    public static Archive<?> deployReaderWriterCurlyBraces() {
        WebArchive war = TestUtil.prepareArchive(ReaderWriterCurlyBraces.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ReaderWriterCurlyBraces.class);
    }

    @Deployment(name = PriorityDeploymenetName)
    public static Archive<?> deployCustomWriterWithPriority() {
        WebArchive war = TestUtil.prepareArchive(PriorityDeploymenetName);
        war.addClass(ReaderWriterCustomer.class);
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ReaderWriterLowPriorityCustomerWriter.class,
                ReaderWriterCustomerWriter.class, ReaderWriterHignPriorityCustomerWriter.class, ReaderWriterResource.class);
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-144
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test144() throws Exception {
        WebTarget base = client
                .target(PortProviderUtil.generateURL("/implicit", ReaderWriterCustomerWriter.class.getSimpleName()));
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("application/xml;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        String s = new String(response.readEntity(byte[].class), "US-ASCII");
        Assertions.assertEquals("<customer><name>bill</name></customer>", s,
                "Response contains wrong content");
        response.close();

        response = base.request().delete();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        ReaderWriterClient proxy = client
                .target(PortProviderUtil.generateBaseUrl(ReaderWriterCustomerWriter.class.getSimpleName()))
                .proxy(ReaderWriterClient.class);
        response = proxy.deleteCustomer();
        response.close();

        response = proxy.deleteComplex();
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-61
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJdkURLConnection() throws Exception {
        URL url = new URL(PortProviderUtil.generateURL("/simple", ReaderWriterResource.class.getSimpleName()));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        @SuppressWarnings("unused")
        Object obj = conn.getContent();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-24 and RESTEASY-139
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test24() throws Exception {
        WebTarget base = client.target(PortProviderUtil.generateURL("/complex", ReaderWriterResource.class.getSimpleName()));
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_FOUND, response.getStatus());
        Assertions.assertEquals(response.getStringHeaders().getFirst("content-type"), "text/plain;charset=UTF-8");
        byte[] responseBody = response.readEntity(byte[].class);
        String responseString = new String(responseBody, "US-ASCII");
        Assertions.assertEquals("hello world", responseString, "Response contains wrong content");

    }

    /**
     * @tpTestDetails Regression test for RESTEASY-1 and RESTEASY-2
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test1and2() throws Exception {
        WebTarget base = client.target(PortProviderUtil.generateURL("/simple", ReaderWriterResource.class.getSimpleName()));
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        String s = new String(response.readEntity(byte[].class), "US-ASCII");
        Assertions.assertEquals("hello world", s, "Response contains wrong content");
    }

    /**
     * @tpTestDetails Test for resource mapped to nowhere using proxy.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testItNewProxy() throws Exception {
        ReaderWriterNowhereClient proxy = ProxyBuilder.builder(ReaderWriterNowhereClient.class,
                client.target(PortProviderUtil.generateBaseUrl("app"))).build();
        Response response = proxy.read();
        response.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-212
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test212() throws Exception {
        WebTarget base = client.target(
                PortProviderUtil.generateURL("/spaces/with%20spaces/without", ReaderWriterSpaces.class.getSimpleName()));
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-227
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ReaderWriterCurlyBraces")
    public void test227() throws Exception {
        WebTarget base = client
                .target(PortProviderUtil.generateURL("/curly/abcd", ReaderWriterCurlyBraces.class.getSimpleName()));
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    @Test
    @OperateOnDeployment(PriorityDeploymenetName)
    public void testProviderWithPriority() throws Exception {
        WebTarget base = client.target(PortProviderUtil.generateURL("/priority", PriorityDeploymenetName));
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("application/xml;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        String s = new String(response.readEntity(byte[].class), "UTF-8");
        Assertions.assertEquals("<customer><name>high priority</name></customer>", s,
                "Response contains wrong content");
        response.close();
    }
}
