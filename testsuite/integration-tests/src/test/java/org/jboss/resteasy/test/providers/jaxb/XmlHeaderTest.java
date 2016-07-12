package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlHeaderResource;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlHeaderDecorator;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlHeaderDecorator2;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlHeaderJunk2Intf;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlHeaderJunkIntf;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlHeaderThing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class XmlHeaderTest {

    private final Logger logger = Logger.getLogger(XmlHeaderTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(XmlHeaderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, XmlHeaderResource.class, XmlHeaderDecorator.class,
                XmlHeaderDecorator2.class, XmlHeaderJunk2Intf.class, XmlHeaderJunkIntf.class, XmlHeaderThing.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, XmlHeaderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails This tests decorators in general with the @XmlHeader annotation
     * @tpPassCrit The response contains expected xml-stylesheet header
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHeader() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test/header"));
        String response = target.request().get(String.class);
        logger.info(response);
        Assert.assertTrue("The response doesn't contain the expected xml-stylesheet header",
                response.contains("<?xml-stylesheet"));

    }

    /**
     * @tpTestDetails This tests decorators in general with the @Stylesheet annotation
     * @tpPassCrit The response contains expected xml-stylesheet header
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStylesheet() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test/stylesheet"));
        String response = target.request().get(String.class);
        logger.info(response);
        Assert.assertTrue("The response doesn't contain the expected xml-stylesheet header",
                response.contains("<?xml-stylesheet"));

    }

}
