package org.jboss.resteasy.test.response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.response.resource.InheritedContextNewService;
import org.jboss.resteasy.test.response.resource.InheritedContextNewSubService;
import org.jboss.resteasy.test.response.resource.InheritedContextService;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-952
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InheritedContextTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(InheritedContextTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, InheritedContextService.class,
                InheritedContextNewService.class, InheritedContextNewSubService.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InheritedContextTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test basic resource with no inheritance
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContext() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/super/test/BaseService")).request();
        Response response = request.get();
        String s = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("true", s);
        response.close();
    }

    /**
     * @tpTestDetails Test basic resource with one level of inheritance
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInheritedContextOneLevel() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/sub/test/SomeService")).request();
        Response response = request.get();
        String s = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("true", s);
        response.close();
    }

    /**
     * @tpTestDetails Test basic resource with two levels of inheritance
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInheritedContextTwoLevels() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/subsub/test/SomeSubService")).request();
        Response response = request.get();
        String s = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("true", s);
        response.close();
    }
}
