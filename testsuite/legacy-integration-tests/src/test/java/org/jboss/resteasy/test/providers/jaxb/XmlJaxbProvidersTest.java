package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jaxb.resource.Order;
import org.jboss.resteasy.test.providers.jaxb.resource.Ordertype;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJaxbProvidersOrderClient;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJaxbProvidersOrderResource;
import org.jboss.resteasy.test.providers.jaxb.resource.ShipTo;
import org.jboss.resteasy.test.providers.jaxb.resource.Shiptotype;
import org.jboss.resteasy.test.providers.jaxb.resource.Item;
import org.jboss.resteasy.test.providers.jaxb.resource.Itemtype;
import org.jboss.resteasy.test.providers.jaxb.resource.JAXBCache;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJaxbProvidersHelper;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlStreamFactory;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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
        return TestUtil.finishContainerPrepare(war, null, XmlJaxbProvidersOrderResource.class, Order.class, Ordertype.class,
                ShipTo.class, Shiptotype.class, Item.class, Itemtype.class, JAXBCache.class, XmlJaxbProvidersHelper.class, XmlStreamFactory.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
        proxy = ProxyBuilder.builder(XmlJaxbProvidersOrderClient.class, client.target(generateURL("/"))).build();
    }

    @After
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

        Assert.assertNotNull(ERR_NULL_ENTITY, order);
        Assert.assertEquals(ERR_CONTENT, "Ryan J. McDonough", order.getPerson());
    }

    /**
     * @tpTestDetails An xml file is loaded on the server and jaxb converts the xml entity Order from xml file into an
     * object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetOrder() {
        Order order = proxy.getOrderById("order_123");
        Assert.assertEquals(ERR_CONTENT, "Ryan J. McDonough", order.getPerson());
    }

    /**
     * @tpTestDetails Clients sends request with order if and  set xml headerr. An xml file is loaded on the server
     * and jaxb converts the xml entity Order from xml file into an object.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetOrderAndUnmarshal() throws Exception {
        Response response = client.target(generateURL("/jaxb/orders") + "/order_123").request()
                .header(XmlJaxbProvidersHelper.FORMAT_XML_HEADER, "true").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        JAXBContext jaxb = JAXBContext.newInstance(Order.class);
        Unmarshaller u = jaxb.createUnmarshaller();
        Order order = (Order) u.unmarshal(response.readEntity(InputStream.class));
        Assert.assertNotNull(ERR_NULL_ENTITY, order);
        Assert.assertEquals(ERR_CONTENT, "Ryan J. McDonough", order.getPerson());
        response.close();
    }

    /**
     * @tpTestDetails Same as testGetOrderWithParams() except that it uses the client framework to implicitly unmarshal
     * the returned order and it tests its value, instead of just printing it out.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetOrderWithParamsToOrder() throws Exception {
        Response response = client.target(generateURL("/jaxb/orders") + "/order_123").request()
                .header(XmlJaxbProvidersHelper.FORMAT_XML_HEADER, "true").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Order order = response.readEntity(Order.class);
        Assert.assertEquals(ERR_CONTENT, "Ryan J. McDonough", order.getPerson());
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
        Assert.assertEquals(ERR_CONTENT, "Ryan J. McDonough", order.getPerson());
        Assert.assertNotSame("The number of items in the Order didn't change after update",
                initialItemCount, order.getItems().size());
        Assert.assertEquals("The number of items in the Order doesn't match", 3, order.getItems().size());
    }
}
