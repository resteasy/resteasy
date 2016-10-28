package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.basic.resource.MultiInterfaceResLocatorResource;
import org.jboss.resteasy.test.resource.basic.resource.MultiInterfaceResLocatorSubresource;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResConcreteSubImpl;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResDoubleInterface;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResGenericInterface;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResGenericSub;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResInternalInterface;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResRoot;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResRootImpl;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResSub;
import org.jboss.resteasy.test.resource.basic.resource.ParameterSubResSubImpl;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.Types;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test resources with sub-resources with parameters.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ParameterSubResTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ParameterSubResTest.class.getSimpleName());
        war.addClass(MultiInterfaceResLocatorResource.class);
        war.addClass(MultiInterfaceResLocatorSubresource.class);
        war.addClass(ParameterSubResConcreteSubImpl.class);
        war.addClass(ParameterSubResDoubleInterface.class);
        war.addClass(ParameterSubResGenericInterface.class);
        war.addClass(ParameterSubResInternalInterface.class);
        war.addClass(ParameterSubResRoot.class);
        war.addClass(ParameterSubResSub.class);
        war.addClass(ParameterSubResSubImpl.class);
        return TestUtil.finishContainerPrepare(war, null, ParameterSubResRootImpl.class, ParameterSubResGenericSub.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ParameterSubResTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check types of parameter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testParametized() throws Exception {
        Types.findParameterizedTypes(ParameterSubResConcreteSubImpl.class, ParameterSubResInternalInterface.class);
    }

    /**
     * @tpTestDetails Check sub resources.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSubResource() throws Exception {
        Response response = client.target(generateURL("/path/sub/fred")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong content of response", "Boo! - fred", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Check root resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRoot() throws Exception {
        Response response = client.target(generateURL("/generic/sub")).queryParam("foo", "42.0").request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong content of response", "42.0", response.readEntity(String.class));
    }
}
