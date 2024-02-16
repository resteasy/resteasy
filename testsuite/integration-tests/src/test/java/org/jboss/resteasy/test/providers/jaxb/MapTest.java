package org.jboss.resteasy.test.providers.jaxb;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.MapFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.MapJaxb;
import org.jboss.resteasy.test.providers.jaxb.resource.MapResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Element;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MapTest {

    private static Logger logger = Logger.getLogger(MapTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MapTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MapFoo.class, MapJaxb.class, MapResource.class);
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
        return PortProviderUtil.generateURL(path, MapTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests marshalling and unmarshalling jaxb object into/from map
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMap() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<resteasy:map xmlns=\"http://foo.com\" xmlns:resteasy=\"http://jboss.org/resteasy\">"
                + "<resteasy:entry key=\"bill\"><mapFoo name=\"hello\"/></resteasy:entry>"
                + "</resteasy:map>";

        JAXBContext ctx = JAXBContext.newInstance(MapJaxb.class, MapJaxb.Entry.class, MapFoo.class);

        MapJaxb map = new MapJaxb("entry", "key", "http://jboss.org/resteasy");
        map.addEntry("bill", new MapFoo("hello"));

        JAXBElement<MapJaxb> element = new JAXBElement<MapJaxb>(new QName("http://jboss.org/resteasy", "map", "resteasy"),
                MapJaxb.class, map);

        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(element, writer);
        Assertions.assertEquals(xml, writer.toString());

        ByteArrayInputStream is = new ByteArrayInputStream(writer.toString().getBytes());
        StreamSource source = new StreamSource(is);
        JAXBContext ctx2 = JAXBContext.newInstance(MapJaxb.class);
        element = ctx2.createUnmarshaller().unmarshal(source, MapJaxb.class);

        Element entry = (Element) element.getValue().getValue().get(0);

        JAXBContext ctx3 = JAXBContext.newInstance(MapJaxb.Entry.class);
        JAXBElement<MapJaxb.Entry> e = ctx3.createUnmarshaller().unmarshal(entry, MapJaxb.Entry.class);
    }

    /**
     * @tpTestDetails Tests Jaxb object which is send to the server and from server, the response is read by using GenericType
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProvider() throws Exception {
        String xml = "<resteasy:map xmlns:resteasy=\"http://jboss.org/resteasy\">"
                + "<resteasy:entry key=\"bill\" xmlns=\"http://foo.com\">"
                + "<mapFoo name=\"bill\"/></resteasy:entry>"
                + "<resteasy:entry key=\"monica\" xmlns=\"http://foo.com\">"
                + "<mapFoo name=\"monica\"/></resteasy:entry>"
                + "</resteasy:map>";

        ResteasyWebTarget target = client.target(generateURL("/map"));

        Map<String, MapFoo> entity = target.request().post(Entity.xml(xml), new GenericType<Map<String, MapFoo>>() {
        });
        Assertions.assertEquals(2, entity.size(), "The response from the server has unexpected content");
        Assertions.assertNotNull(entity.get("bill"), "The response from the server has unexpected content");
        Assertions.assertNotNull(entity.get("monica"), "The response from the server has unexpected content");
        Assertions.assertEquals(entity.get("bill").getName(), "bill", "The response from the server has unexpected content");
        Assertions.assertEquals(entity.get("monica").getName(),
                "monica", "The response from the server has unexpected content");

        String entityString = target.request().post(Entity.xml(xml), String.class);
        logger.info(entityString);

    }

    /**
     * @tpTestDetails Tests Jaxb object which is send to the server and from server, the response is read by using GenericType,
     *                The tested entity contains integer key types
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProviderMapIntegerFoo() throws Exception {
        String xml = "<resteasy:map xmlns:resteasy=\"http://jboss.org/resteasy\">"
                + "<resteasy:entry key=\"1\" xmlns=\"http://foo.com\">"
                + "<mapFoo name=\"bill\"/></resteasy:entry>"
                + "<resteasy:entry key=\"2\" xmlns=\"http://foo.com\">"
                + "<mapFoo name=\"monica\"/></resteasy:entry>"
                + "</resteasy:map>";

        ResteasyWebTarget target = client.target(generateURL("/map/integerFoo"));
        Response response = target.request().post(Entity.xml(xml));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        Map<String, MapFoo> entity = response.readEntity(new GenericType<Map<String, MapFoo>>() {
        });
        Assertions.assertEquals(2, entity.size(), "The response from the server has unexpected content");
        Assertions.assertNotNull(entity.get("1"), "The response from the server has unexpected content");
        Assertions.assertNotNull(entity.get("2"), "The response from the server has unexpected content");
        Assertions.assertEquals(entity.get("1").getName(), "bill", "The response from the server has unexpected content");
        Assertions.assertEquals(entity.get("2").getName(), "monica", "The response from the server has unexpected content");

        String entityString = target.request().post(Entity.xml(xml), String.class);

        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<map xmlns:ns2=\"http://foo.com\">"
                + "<entry key=\"1\"><ns2:mapFoo name=\"bill\"/></entry>"
                + "<entry key=\"2\"><ns2:mapFoo name=\"monica\"/></entry>"
                + "</map>";
        Assertions.assertEquals(result, entityString);
    }

    /**
     * @tpTestDetails Tests Jaxb object which is send to the server and from server, the response is read by using GenericType,
     *                the resource is annotated with @WrappedMap annotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWrapped() throws Exception {
        String xml = "<map xmlns:mapFoo=\"http://foo.com\">"
                + "<entry key=\"bill\">"
                + "<mapFoo:mapFoo name=\"bill\"/></entry>"
                + "<entry key=\"monica\">"
                + "<mapFoo:mapFoo name=\"monica\"/></entry>"
                + "</map>";

        ResteasyWebTarget target = client.target(generateURL("/map/wrapped"));
        Map<String, MapFoo> entity = target.request().post(Entity.xml(xml), new GenericType<Map<String, MapFoo>>() {
        });

        Assertions.assertEquals(2, entity.size(), "The response from the server has unexpected content");
        Assertions.assertNotNull(entity.get("bill"), "The response from the server has unexpected content");
        Assertions.assertNotNull(entity.get("monica"), "The response from the server has unexpected content");
        Assertions.assertEquals(entity.get("bill").getName(), "bill", "The response from the server has unexpected content");
        Assertions.assertEquals(entity.get("monica").getName(),
                "monica", "The response from the server has unexpected content");

    }

    /**
     * @tpTestDetails Tests that Jaxb object with wrong structure returns bad request (400) response code
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadWrapped() throws Exception {
        String xml = "<resteasy:map xmlns:resteasy=\"http://jboss.org/resteasy\">"
                + "<resteasy:entry key=\"bill\" xmlns=\"http://foo.com\">"
                + "<mapFoo name=\"bill\"/></resteasy:entry>"
                + "<resteasy:entry key=\"monica\" xmlns=\"http://foo.com\">"
                + "<mapFoo name=\"monica\"/></resteasy:entry>"
                + "</resteasy:map>";

        ResteasyWebTarget target = client.target(generateURL("/map/wrapped"));
        Response response = target.request().post(Entity.xml(xml));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());

    }
}
