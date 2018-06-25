package org.jboss.resteasy.test.providers.jackson2.jsonfilter;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailing;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2Person;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2PersonResource;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.JsonFilterModifierMultipleWriteInterceptor;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectFilterModifierMultiple;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.PersonType;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Filters fields from json object. Sets ObjectWriterModifier in the interceptor.
 * The filter filters field personType of Jackson2Person pojo. The ObjectWriterModifier has multiple filters registered.
 * Only one is set to for Json2Person pojo.
 * @tpSince RESTEasy 3.1.0
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({NotForForwardCompatibility.class, ExpectedFailingOnWildFly13.class, ExpectedFailing.class}) //RESTEASY-1933
public class JsonFilterWithInterceptorMultipleFiltersTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonFilterWithInterceptorMultipleFiltersTest.class.getSimpleName());
        war.addClasses(Jackson2Person.class, PersonType.class, ObjectFilterModifierMultiple.class);
        war.addAsManifestResource(new StringAsset("Manifest-Version: 1.0\n" + "Dependencies: com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider\n"), "MANIFEST.MF");
        return TestUtil.finishContainerPrepare(war, null, Jackson2PersonResource.class, JsonFilterModifierMultipleWriteInterceptor.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JsonFilterWithInterceptorMultipleFiltersTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails  Correct filter is used when multiple filters available
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonString2() throws Exception {
        Client client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(generateURL("/person/333"));
        Response response = target.request().get();
        response.bufferEntity();
        Assert.assertTrue("Multiple filter doesn't work", !response.readEntity(String.class).contains("id") &&
                !response.readEntity(String.class).contains("name") &&
                !response.readEntity(String.class).contains("address") &&
                response.readEntity(String.class).contains("personType"));
        client.close();
    }
}
