package org.jboss.resteasy.test.providers.jackson2;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonResource;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonType1;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonSubResourceIntf;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonSubResourceSubIntf;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonAbstractParent;
import org.jboss.resteasy.test.providers.jackson2.resource.ProxyWithGenericReturnTypeJacksonType2;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({ExpectedFailingOnWildFly13.class})
public class ProxyWithGenericReturnTypeJacksonTest {

    protected static final Logger logger = Logger.getLogger(ProxyWithGenericReturnTypeJacksonTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
        war.addClass(Jackson2Test.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyWithGenericReturnTypeJacksonAbstractParent.class,
                ProxyWithGenericReturnTypeJacksonResource.class, ProxyWithGenericReturnTypeJacksonSubResourceIntf.class,
                ProxyWithGenericReturnTypeJacksonSubResourceSubIntf.class, ProxyWithGenericReturnTypeJacksonType1.class,
                ProxyWithGenericReturnTypeJacksonType2.class);
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
        return PortProviderUtil.generateURL(path, ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests usage of proxied subresource
     * @tpPassCrit The resource returns Success response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxyWithGenericReturnType() throws Exception {
        WebTarget target = client.target(generateURL("/test/one/"));
        logger.info("Sending request");
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info("Received response: " + entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Type property is missing.", entity.contains("type"));
        response.close();

        target = client.target(generateURL("/test/list/"));
        logger.info("Sending request");
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received response: " + entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Type property is missing.", entity.contains("type"));
        response.close();
    }
}
