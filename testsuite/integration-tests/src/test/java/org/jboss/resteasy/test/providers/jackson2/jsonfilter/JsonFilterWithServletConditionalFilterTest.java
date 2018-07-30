package org.jboss.resteasy.test.providers.jackson2.jsonfilter;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2Product;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2Resource;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectFilterModifierConditional;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectWriterModifierConditionalFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Filters fields from json object. Specifies the filter implementation class in web.xml.
 * The filter filters field of Jackson2Product pojo upon value if its 'id' field. Pojo with id value < 0 is filtered
 * out and not returned in the response. See http://www.baeldung.com/jackson-serialize-field-custom-criteria
 * @tpInfo RESTESY-1442
 * @tpSince RESTEasy 3.0.20.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({NotForForwardCompatibility.class})
public class JsonFilterWithServletConditionalFilterTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonFilterWithServletConditionalFilterTest.class.getSimpleName());
        war.addClasses(Jackson2Product.class, ObjectFilterModifierConditional.class, ObjectWriterModifierConditionalFilter.class);
        war.addAsManifestResource(new StringAsset("Manifest-Version: 1.0\n" + "Dependencies: com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider\n"), "MANIFEST.MF");
        war.addAsWebInfResource(JsonFilterWithServletConditionalFilterTest.class.getPackage(), "web-filter-conditional.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, Jackson2Resource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JsonFilterWithServletConditionalFilterTest.class.getSimpleName());
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
     * @tpTestDetails Json field id is filtered out
     * @tpSince RESTEasy 3.0.20.Final
     */
    @Test
    @Category({ExpectedFailingOnWildFly13.class})
    public void testJacksonConditionalStringPropertyFiltered() throws Exception {
        WebTarget target = client.target(generateURL("/products/-1"));
        Response response = target.request().get();
        response.bufferEntity();
        Assert.assertTrue("Conditional filter doesn't work", !response.readEntity(String.class).contains("id") &&
                response.readEntity(String.class).contains("name"));
    }

    /**
     * @tpTestDetails Json field id is not filtered
     * @tpSince RESTEasy 3.0.20.Final
     */
    @Test
    public void testJacksonConditionalStringPropertyNotFiltered() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        response.bufferEntity();
        Assert.assertTrue("Conditional filter doesn't work", response.readEntity(String.class).contains("id") &&
                response.readEntity(String.class).contains("name"));
    }
}
