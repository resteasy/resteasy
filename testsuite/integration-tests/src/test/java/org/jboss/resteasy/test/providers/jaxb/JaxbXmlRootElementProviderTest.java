package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbElementClient;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbJsonXmlRootElementClient;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbXmlRootElementClient;
import org.jboss.resteasy.test.providers.jaxb.resource.Parent;
import org.jboss.resteasy.test.providers.jaxb.resource.Child;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbJsonElementClient;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbJunkXmlOrderClient;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbXmlRootElementProviderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ResponseProcessingException;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JaxbXmlRootElementProviderTest {

    private String JAXB_URL = generateURL("/jaxb");
    private static final String JSON_PARENT = "JSON Parent";
    private static final String XML_PARENT = "XML Parent";
    private static Logger logger = Logger.getLogger(XmlHeaderTest.class.getName());

    private static final String ERR_PARENT_NULL = "Parent is null";
    private static final String ERR_PARENT_NAME = "The name of the parent is not the expected one";

    static ResteasyClient client;
    private JaxbXmlRootElementClient jaxbClient;
    private JaxbElementClient jaxbElementClient;
    private JaxbJsonXmlRootElementClient jsonClient;
    private JaxbJsonElementClient jsonElementClient;
    private JaxbJunkXmlOrderClient junkClient;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JaxbXmlRootElementProviderTest.class.getSimpleName());
        war.addClass(Parent.class);
        war.addClass(Child.class);
        return TestUtil.finishContainerPrepare(war, null, JaxbXmlRootElementProviderResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
        jaxbClient = ProxyBuilder.builder(JaxbXmlRootElementClient.class, client.target(JAXB_URL)).build();
        jaxbElementClient = ProxyBuilder.builder(JaxbElementClient.class, client.target(JAXB_URL)).build();
        jsonClient = ProxyBuilder.builder(JaxbJsonXmlRootElementClient.class, client.target(JAXB_URL)).build();
        jsonElementClient = ProxyBuilder.builder(JaxbJsonElementClient.class, client.target(JAXB_URL)).build();
        junkClient = ProxyBuilder.builder(JaxbJunkXmlOrderClient.class, client.target(JAXB_URL)).build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxbXmlRootElementProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Resteasy proxy client sends get request for jaxb annotated class, the response is expected to be in xml format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetParent() {
        Parent parent = jaxbClient.getParent(XML_PARENT);
        Assert.assertEquals(ERR_PARENT_NAME, parent.getName(), XML_PARENT);
    }

    /**
     * @tpTestDetails Resteasy proxy client sends get request for jaxb annotated class, the response is expected to be in xml format,
     * client proxy with @Produces ""application/junk+xml" is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetParentJunk() {
        Parent parent = junkClient.getParent(XML_PARENT);
        Assert.assertEquals(ERR_PARENT_NAME, parent.getName(), XML_PARENT);
    }

    /**
     * @tpTestDetails Resteasy proxy client sends get request for jaxb annotated class, the response is expected to convert
     * into JAXBElement<Parent>
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetParentElement() {
        JAXBElement<Parent> element = jaxbElementClient.getParent(XML_PARENT);
        Parent parent = element.getValue();
        Assert.assertEquals(ERR_PARENT_NAME, parent.getName(), XML_PARENT);
    }

    /**
     * @tpTestDetails Resteasy proxy client sends get request for jaxb annotated class, the response is expected to be in
     * json format. Regression test for JBEAP-3530.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetParentJson() throws Exception {
        Parent parent = null;
        try {
            parent = jsonClient.getParent(JSON_PARENT);
        } catch (ResponseProcessingException exc) {
            Assert.fail(String.format("Regression of JBEAP-3530, see %s", exc.getCause().toString()));
        }
        Assert.assertNotNull(ERR_PARENT_NULL, parent);
        Assert.assertEquals(ERR_PARENT_NAME, parent.getName(), JSON_PARENT);

        String mapped = jsonClient.getParentString(JSON_PARENT);
        Assert.assertEquals("Wrong response from the server",
                "{\"name\":\"JSON Parent\",\"child\":[{\"name\":\"Child 1\"},{\"name\":\"Child 2\"},{\"name\":\"Child 3\"}]}", mapped);
    }

    /**
     * @tpTestDetails Resteasy proxy client sends post request with jaxb annotated object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostParent() {
        jaxbClient.postParent(Parent.createTestParent("TEST"));
    }

    /**
     * @tpTestDetails Resteasy proxy client sends post request with JAXBElement object containing jaxb annotated object instance
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostParentElement() {
        Parent parent = Parent.createTestParent("TEST ELEMENT");
        JAXBElement<Parent> parentElement = new JAXBElement<Parent>(new QName("parent"),
                Parent.class, parent);
        jaxbElementClient.postParent(parentElement);
    }

}
