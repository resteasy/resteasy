package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.form.resource.ComplexFormAddress;
import org.jboss.resteasy.test.form.resource.ComplexFormPerson;
import org.jboss.resteasy.test.form.resource.ComplexFormResource;
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
 * @tpTestCaseDetails Test complex inner form parameters. Check return value, it is based on form.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ComplexFormTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(CollectionsFormTest.class.getSimpleName());
        war.addClasses(ComplexFormPerson.class, ComplexFormAddress.class);
        return TestUtil.finishContainerPrepare(war, null, ComplexFormResource.class);
    }

    /**
     * @tpTestDetails Set all relevant parameters to form and check return value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldSupportNestedForm() throws Exception {
        javax.ws.rs.core.Form form = new javax.ws.rs.core.Form()
        .param("name", "John Doe")
        .param("invoice.street", "Main Street")
        .param("shipping.street", "Station Street");

        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget base = client.target(PortProviderUtil.generateURL("/person", CollectionsFormTest.class.getSimpleName()));
        Response response = base.request().accept(MediaType.TEXT_PLAIN).post(Entity.form(form));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong content of response", "name:'John Doe', invoice:'Main Street', shipping:'Station Street'", response.readEntity(String.class));
    }
}
