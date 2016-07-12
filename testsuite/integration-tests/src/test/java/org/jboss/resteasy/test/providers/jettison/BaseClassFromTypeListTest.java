package org.jboss.resteasy.test.providers.jettison;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jettison.resource.BaseClassFromTypeListStoreIntf;
import org.jboss.resteasy.test.providers.jettison.resource.BaseClassFromTypeListCustomer;
import org.jboss.resteasy.test.providers.jettison.resource.BaseClassFromTypeListInAccountsIntf;
import org.jboss.resteasy.test.providers.jettison.resource.BaseClassFromTypeListResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-167
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class BaseClassFromTypeListTest {

    public static class Parent<T> {
        public List<T> get() {
            return null;
        }
    }

    public static class Child extends Parent<BaseClassFromTypeListCustomer> {
    }

    protected final Logger logger = Logger.getLogger(BaseClassFromTypeListTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(BaseClassFromTypeListTest.class.getSimpleName());
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, BaseClassFromTypeListCustomer.class, BaseClassFromTypeListResource.class,
                BaseClassFromTypeListInAccountsIntf.class, BaseClassFromTypeListStoreIntf.class);
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
        return PortProviderUtil.generateURL(path, BaseClassFromTypeListTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test with resource implementing generic interface
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIntfTemplate() throws Exception {
        WebTarget target = client.target(generateURL("/intf"));
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        logger.info(str);
        response = target.request().put(Entity.entity(str, "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
    }
}
