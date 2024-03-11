package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultipleAcceptHeaderTest {

    protected static String APPLICATION_JSON = "Content-Type: application/json";

    protected static String APPLICATION_XML = "Content-Type: application/xml";

    private TestInterfaceClient service;
    private Client client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MultipleAcceptHeaderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TestResourceServer.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultipleAcceptHeaderTest.class.getSimpleName());
    }

    @Path("/test")
    public static class TestResourceServer {
        @GET
        @Path("accept")
        @Produces("application/json")
        public String acceptJson() {
            return APPLICATION_JSON;
        }

        @GET
        @Path("accept")
        @Produces({ "application/xml", "text/plain" })
        public String acceptXml() {
            return APPLICATION_XML;
        }
    }

    @Path("test")
    interface TestInterfaceClient {
        @GET
        @Path("accept")
        @Produces("application/json")
        String getJson();

        @GET
        @Path("accept")
        @Produces("application/xml")
        String getXml();

        @GET
        @Path("accept")
        @Produces({ "application/wrong1", "application/wrong2", "application/xml" })
        String getXmlMultiple();

        @GET
        @Path("accept")
        @Produces({ "application/wrong1", "text/plain" })
        String getXmlPlainMultiple();
    }

    @BeforeEach
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateBaseUrl());
        service = target.proxy(TestInterfaceClient.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        client.close();
        client = null;
    }

    @Test
    public void testSingleAcceptHeader() throws Exception {
        String result = service.getJson();
        Assertions.assertEquals(APPLICATION_JSON, result);
    }

    @Test
    public void testSingleAcceptHeader2() throws Exception {
        String result = service.getXml();
        Assertions.assertEquals(APPLICATION_XML, result);
    }

    @Test
    public void testMultipleAcceptHeader() throws Exception {
        String result = service.getXmlMultiple();
        Assertions.assertEquals(APPLICATION_XML, result);
    }

    @Test
    public void testMultipleAcceptHeaderSecondHeader() throws Exception {
        String result = service.getXmlPlainMultiple();
        Assertions.assertEquals(APPLICATION_XML, result);
    }
}
