package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.form.resource.CollectionsFormAddress;
import org.jboss.resteasy.test.form.resource.CollectionsFormPerson;
import org.jboss.resteasy.test.form.resource.CollectionsFormResource;
import org.jboss.resteasy.test.form.resource.CollectionsFormTelephoneNumber;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test inner form parameters and collections.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CollectionsFormTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(CollectionsFormTest.class.getSimpleName());
        war.addClasses(CollectionsFormPerson.class, CollectionsFormTelephoneNumber.class,
                CollectionsFormAddress.class);
        return TestUtil.finishContainerPrepare(war, null, CollectionsFormResource.class);
    }

    /**
     * @tpTestDetails Set all relevant parameters to form.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldSupportCollectionsInForm() throws Exception {
        javax.ws.rs.core.Form form = new javax.ws.rs.core.Form()
        .param("telephoneNumbers[0].countryCode", "31")
        .param("telephoneNumbers[0].number", "0612345678")
        .param("telephoneNumbers[1].countryCode", "91")
        .param("telephoneNumbers[1].number", "9717738723")
        .param("address[INVOICE].street", "Main Street")
        .param("address[INVOICE].houseNumber", "2")
        .param("address[SHIPPING].street", "Square One")
        .param("address[SHIPPING].houseNumber", "13");

        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget base = client.target(PortProviderUtil.generateURL("/person", CollectionsFormTest.class.getSimpleName()));
        Response response = base.request().accept(MediaType.TEXT_PLAIN).post(Entity.form(form));

        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
    }
}
