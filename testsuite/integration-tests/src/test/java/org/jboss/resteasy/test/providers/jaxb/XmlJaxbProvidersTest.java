package org.jboss.resteasy.test.providers.jaxb;

import java.io.File;
import java.io.FilePermission;
import java.io.InputStream;
import java.lang.reflect.ReflectPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.Item;
import org.jboss.resteasy.test.providers.jaxb.resource.Itemtype;
import org.jboss.resteasy.test.providers.jaxb.resource.JAXBCache;
import org.jboss.resteasy.test.providers.jaxb.resource.Order;
import org.jboss.resteasy.test.providers.jaxb.resource.Ordertype;
import org.jboss.resteasy.test.providers.jaxb.resource.ShipTo;
import org.jboss.resteasy.test.providers.jaxb.resource.Shiptotype;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJaxbProvidersHelper;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJaxbProvidersOrderClient;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJaxbProvidersOrderResource;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlStreamFactory;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class XmlJaxbProvidersTest {

    private XmlJaxbProvidersOrderClient proxy;
    static ResteasyClient client;

    private static final String ERR_NULL_ENTITY = "The entity returned from the server was null";
    private static final String ERR_CONTENT = "Unexpected content of the Order";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(XmlJaxbProvidersTest.class.getSimpleName());
        war.addClass(XmlJaxbProvidersTest.class);
        war.addAsResource(XmlJaxbProvidersTest.class.getPackage(), "orders/order_123.xml");
        war.as(ZipExporter.class).exportTo(new File("target", XmlJaxbProvidersTest.class.getSimpleName() + ".war"), true);

        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new FilePermission("<<ALL FILES>>", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("getClassLoader")),
                "permissions.xml");

        return TestUtil.finishContainerPrepare(war, null, XmlJaxbProvidersOrderResource.class, Order.class, Ordertype.class,
                ShipTo.class, Shiptotype.class, Item.class, Itemtype.class, JAXBCache.class, XmlJaxbProvidersHelper.class,
                XmlStreamFactory.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
        proxy = ProxyBuilder.builder(XmlJaxbProvidersOrderClient.class, client.target(generateURL("/"))).build();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, XmlJaxbProvidersTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test jaxb unmarshaller to correctly unmarshall InputStream
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUnmarshalOrder() throws Exception {
        InputStream in = XmlJaxbProvidersTest.class.getResourceAsStream("orders/order_123.xml");
        Order order = XmlJaxbProvidersHelper.unmarshall(Order.class, in).getValue();

        Assertions.assertNotNull(order, ERR_NULL_ENTITY);
        Assertions.assertEquals("Ryan J. McDonough", order.getPerson(), ERR_CONTENT);
    }

    /**
     * @tpTestDetails An xml file is loaded on the server and jaxb converts the xml entity Order from xml file into an
     *                object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetOrder() {
        Order order = proxy.getOrderById("order_123");
        Assertions.assertEquals("Ryan J. McDonough", order.getPerson(), ERR_CONTENT);
    }

    /**
     * @tpTestDetails Clients sends request with order if and set xml headerr. An xml file is loaded on the server
     *                and jaxb converts the xml entity Order from xml file into an object.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetOrderAndUnmarshal() throws Exception {
        Response response = client.target(generateURL("/jaxb/orders") + "/order_123").request()
                .header(XmlJaxbProvidersHelper.FORMAT_XML_HEADER, "true").get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        JAXBContext jaxb = JAXBContext.newInstance(Order.class);
        Unmarshaller u = jaxb.createUnmarshaller();
        Order order = (Order) u.unmarshal(response.readEntity(InputStream.class));
        Assertions.assertNotNull(order, ERR_NULL_ENTITY);
        Assertions.assertEquals("Ryan J. McDonough", order.getPerson(), ERR_CONTENT);
        response.close();
    }

    /**
     * @tpTestDetails Same as testGetOrderWithParams() except that it uses the client framework to implicitly unmarshal
     *                the returned order and it tests its value, instead of just printing it out.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetOrderWithParamsToOrder() throws Exception {
        Response response = client.target(generateURL("/jaxb/orders") + "/order_123").request()
                .header(XmlJaxbProvidersHelper.FORMAT_XML_HEADER, "true").get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Order order = response.readEntity(Order.class);
        Assertions.assertEquals("Ryan J. McDonough", order.getPerson(), ERR_CONTENT);
    }

    /**
     * @tpTestDetails Updates the specified order and returns updated object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUpdateOrder() {
        InputStream in = XmlJaxbProvidersTest.class.getResourceAsStream("orders/order_123.xml");
        Order order = XmlJaxbProvidersHelper.unmarshall(Order.class, in).getValue();
        int initialItemCount = order.getItems().size();
        order = proxy.updateOrder(order, "order_123");
        Assertions.assertEquals("Ryan J. McDonough", order.getPerson(), ERR_CONTENT);
        Assertions.assertNotSame(initialItemCount, order.getItems().size(),
                "The number of items in the Order didn't change after update");
        Assertions.assertEquals(3, order.getItems().size(),
                "The number of items in the Order doesn't match");
    }
}
