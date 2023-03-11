package org.jboss.resteasy.test.providers.jackson2.jsonfilter;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.JsonFilterChild;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.JsonFilterChildResource;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.JsonFilterParent;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectFilterModifier;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectWriterModifierFilter;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.PersonType;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Filters fields from json object. Specifies the filter implementation class in web.xml.
 *                    JsonFilterParent defines the @JsonFilter annotation. JsonFilter applies to its subclass JsonFilterChild as
 *                    well.
 * @tpSince RESTEasy 3.1.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonFilterSuperClassTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonFilterSuperClassTest.class.getSimpleName());
        war.addClasses(JsonFilterParent.class, JsonFilterChild.class, PersonType.class, ObjectFilterModifier.class,
                ObjectWriterModifierFilter.class);
        war.addAsManifestResource(
                new StringAsset(
                        "Manifest-Version: 1.0\n" + "Dependencies: com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider\n"),
                "MANIFEST.MF");
        war.addAsWebInfResource(JsonFilterWithSerlvetFilterTest.class.getPackage(), "web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, JsonFilterChildResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JsonFilterSuperClassTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Json string in the response is correctly filtered
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonStringInSuperClass() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/superclass/333"));
        Response response = target.request().get();
        response.bufferEntity();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Filter doesn't work", !response.readEntity(String.class).contains("id") &&
                response.readEntity(String.class).contains("name"));
        client.close();
    }
}
