package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.CustomOverrideWriter;
import org.jboss.resteasy.test.providers.jaxb.resource.CustomOverrideResource;
import org.jboss.resteasy.test.providers.jaxb.resource.CustomOverrideFoo;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CustomOverrideTest {

    private static Logger logger = Logger.getLogger(CustomOverrideTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CustomOverrideTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CustomOverrideResource.class, CustomOverrideWriter.class,
                CustomOverrideFoo.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomOverrideTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for same resource path for media type xml and "text/x-vcard" with custom MessageBodyWriter
     * @tpInfo RESTEASY-510
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRegression() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        String response = target.request().accept("text/x-vcard").get(String.class);
        logger.info(response);
        Assert.assertEquals("---bill---", response);

        response = target.request().accept("application/xml").get(String.class);
        Assert.assertTrue(response.contains("customOverrideFoo"));
        logger.info(response);
    }
}
