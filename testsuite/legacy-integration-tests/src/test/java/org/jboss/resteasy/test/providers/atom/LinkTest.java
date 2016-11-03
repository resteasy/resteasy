package org.jboss.resteasy.test.providers.atom;

import static org.hamcrest.CoreMatchers.containsString;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.atom.resource.LinkProduct;
import org.jboss.resteasy.test.providers.atom.resource.LinkProductService;
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
 * @tpSubChapter Atom provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.plugins.providers.atom.Link class
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class LinkTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(LinkTest.class.getSimpleName());
        war.addClass(LinkProduct.class);
        return TestUtil.finishContainerPrepare(war, null, LinkProductService.class);
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
        return PortProviderUtil.generateURL(path, LinkTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test response as java custom object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRelativeLinkProductOutput() throws Exception {
        Response response = client.target(generateURL("/products/333")).request().get();
        LinkProduct product = response.readEntity(LinkProduct.class);
        Assert.assertEquals("/LinkTest/products/333/self", product.getLinks().get(0).getHref().getPath());
        Assert.assertEquals("/LinkTest/products", product.getLinks().get(1).getHref().getPath());
        response.close();
    }

    /**
     * @tpTestDetails Test response as XML String
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRelativeLinkStringOutput() throws Exception {
        Response response = client.target(generateURL("/products/333")).request().get();
        String stringResponse = response.readEntity(String.class);
        Assert.assertThat("Wrong link in response", stringResponse, containsString("/LinkTest/products/333/self\""));
        Assert.assertThat("Wrong link in response", stringResponse, containsString("/LinkTest/products\""));
        response.close();
    }
}
