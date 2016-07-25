package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlEnumParamLocation;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlEnumParamResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.runner.RunWith;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.PortProviderUtil;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class XmlEnumParamTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(XmlEnumParamTest.class.getSimpleName());
        war.addClass(XmlEnumParamTest.class);
        return TestUtil.finishContainerPrepare(war, null, XmlEnumParamResource.class, XmlEnumParamLocation.class);
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
        return PortProviderUtil.generateURL(path, XmlEnumParamTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests xml enum param in the resource
     * @tpPassCrit The expected enum type is returned
     * @tpInfo RESTEASY-428
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlEnumParam() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/enum"));
        String response = target.queryParam("loc", "north").request().get(String.class);
        Assert.assertEquals("The response doesn't contain expected enum type", "NORTH", response.toUpperCase());
    }

}
