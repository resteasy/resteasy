package org.jboss.resteasy.test.providers.jettison;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jettison.resource.JettisonCustomer;
import org.jboss.resteasy.test.providers.jettison.resource.JettisonCustomerList;
import org.jboss.resteasy.test.providers.jettison.resource.JettisonCustomerManagementResource;
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

import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-175
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JettisonCustomerTest {

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JettisonCustomerTest.class.getSimpleName());
        war.addClasses(JettisonCustomer.class, JettisonCustomerList.class);
        return TestUtil.finishContainerPrepare(war, null, JettisonCustomerManagementResource.class);
    }

    @Before
    public void before() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JettisonCustomerTest.class.getSimpleName());
    }

    @Test
    public void testCustomer() {
        ResteasyWebTarget target = client.target(generateURL("/management/customers"));
        Response response = target.request().get();

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        JettisonCustomerList customers = response.readEntity(JettisonCustomerList.class);
        Assert.assertEquals(4, customers.size());

        response.close();
    }
}
