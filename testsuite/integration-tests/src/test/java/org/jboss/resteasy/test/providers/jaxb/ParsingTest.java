package org.jboss.resteasy.test.providers.jaxb;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.parsing.ObjectFactory;
import org.jboss.resteasy.test.providers.jaxb.resource.parsing.ParsingAbstractData;
import org.jboss.resteasy.test.providers.jaxb.resource.parsing.ParsingDataCollectionPackage;
import org.jboss.resteasy.test.providers.jaxb.resource.parsing.ParsingDataCollectionRecord;
import org.jboss.resteasy.test.providers.jaxb.resource.parsing.ParsingStoreResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-143
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ParsingTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ParsingTest.class.getSimpleName());
        war.addClass(ParsingAbstractData.class);
        war.addClass(ParsingDataCollectionPackage.class);
        war.addClass(ParsingDataCollectionRecord.class);
        war.addClass(ObjectFactory.class);
        return TestUtil.finishContainerPrepare(war, null, ParsingStoreResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ParsingTest.class.getSimpleName());
    }

    private static final String XML_CONTENT_DEFAULT_NS = "<ParsingDataCollectionPackage xmlns=\"http://www.example.org/ParsingDataCollectionPackage\">\n"
            + "  <sourceID>System A</sourceID>\n"
            + "  <eventID>Exercise B</eventID>\n"
            + "  <dataRecords>\n"
            + "     <ParsingDataCollectionRecord>\n"
            + "        <timestamp>2008-08-13T12:24:00</timestamp>\n"
            + "        <collectedData>Operator pushed easy button</collectedData>\n"
            + "     </ParsingDataCollectionRecord>\n" + "  </dataRecords>\n" + "</ParsingDataCollectionPackage>";
    private static final String XML_CONTENT = "<ns:ParsingDataCollectionPackage xmlns:ns=\"http://www.example.org/ParsingDataCollectionPackage\">\n"
            + "  <sourceID>System A</sourceID>\n"
            + "  <eventID>Exercise B</eventID>\n"
            + "  <dataRecords>\n"
            + "     <ParsingDataCollectionRecord>\n"
            + "        <timestamp>2008-08-13T12:24:00</timestamp>\n"
            + "        <collectedData>Operator pushed easy button</collectedData>\n"
            + "     </ParsingDataCollectionRecord>\n"
            + "  </dataRecords>\n"
            + "</ns:ParsingDataCollectionPackage>";

    /**
     * @tpTestDetails Check XML parsing
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWire() throws Exception {
        {
            Response response = client.target(generateURL("/storeXML")).request()
                    .post(Entity.entity(XML_CONTENT, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());
            response.close();
        }

        {
            Response response = client.target(generateURL("/storeXML/abstract")).request()
                    .post(Entity.entity(XML_CONTENT, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());
            response.close();
        }
    }
}
