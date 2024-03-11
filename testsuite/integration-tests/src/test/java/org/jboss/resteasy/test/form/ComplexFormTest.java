package org.jboss.resteasy.test.form;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.form.resource.ComplexFormAddress;
import org.jboss.resteasy.test.form.resource.ComplexFormPerson;
import org.jboss.resteasy.test.form.resource.ComplexFormResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test complex inner form parameters. Check return value, it is based on form.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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
        jakarta.ws.rs.core.Form form = new jakarta.ws.rs.core.Form()
                .param("name", "John Doe")
                .param("invoice.street", "Main Street")
                .param("shipping.street", "Station Street");

        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(PortProviderUtil.generateURL("/person", CollectionsFormTest.class.getSimpleName()));
        Response response = base.request().accept(MediaType.TEXT_PLAIN).post(Entity.form(form));

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("name:'John Doe', invoice:'Main Street', shipping:'Station Street'",
                response.readEntity(String.class),
                "Wrong content of response");
        client.close();
    }
}
