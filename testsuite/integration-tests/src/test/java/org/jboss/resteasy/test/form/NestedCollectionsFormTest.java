package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.form.resource.NestedCollectionsFormAddress;
import org.jboss.resteasy.test.form.resource.NestedCollectionsFormCountry;
import org.jboss.resteasy.test.form.resource.NestedCollectionsFormPerson;
import org.jboss.resteasy.test.form.resource.NestedCollectionsFormResource;
import org.jboss.resteasy.test.form.resource.NestedCollectionsFormTelephoneNumber;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test nested form parameters
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NestedCollectionsFormTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(NestedCollectionsFormTest.class.getSimpleName());
        war.addClass(NestedCollectionsFormTelephoneNumber.class);
        war.addClass(NestedCollectionsFormPerson.class);
        war.addClass(NestedCollectionsFormCountry.class);
        war.addClass(NestedCollectionsFormAddress.class);
        return TestUtil.finishContainerPrepare(war, null, NestedCollectionsFormResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, NestedCollectionsFormTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void close() {
        client.close();
    }

    /**
     * @tpTestDetails Set all relevant parameters to form.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldSupportCollectionsWithNestedObjectsInForm() throws Exception {
        javax.ws.rs.core.Form form = new javax.ws.rs.core.Form()
        .param("telephoneNumbers[0].country.code", "31")
        .param("telephoneNumbers[0].number", "0612345678")
        .param("telephoneNumbers[1].country.code", "91")
        .param("telephoneNumbers[1].number", "9717738723")
        .param("address[INVOICE].street", "Main Street")
        .param("address[INVOICE].houseNumber", "2")
        .param("address[INVOICE].country.code", "NL")
        .param("address[SHIPPING].street", "Square One")
        .param("address[SHIPPING].houseNumber", "13")
        .param("address[SHIPPING].country.code", "IN");

        WebTarget base = client.target(PortProviderUtil.generateURL("/person", NestedCollectionsFormTest.class.getSimpleName()));
        Response response = base.request().accept(MediaType.TEXT_PLAIN).post(Entity.form(form));

        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());

        response.close();
    }
}