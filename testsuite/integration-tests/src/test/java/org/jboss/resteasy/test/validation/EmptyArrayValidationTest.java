package org.jboss.resteasy.test.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.EmptyArrayValidationFoo;
import org.jboss.resteasy.test.validation.resource.EmptyArrayValidationResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter resteasy-validator-provider
 * @tpChapter Validation
 * @tpSince RESTEasy 4.6.0
 * @tpTestCaseDetails Regression test for RESTEASY-2765
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EmptyArrayValidationTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EmptyArrayValidationTest.class.getSimpleName());
        war.addClass(EmptyArrayValidationFoo.class);
        return TestUtil.finishContainerPrepare(war, null, EmptyArrayValidationResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EmptyArrayValidationTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Verify validation of empty array doesn't throw ArrayIndexOutOfBoundsException.
     * @tpSince RESTEasy 4.6.0
     */
    @Test
    public void testEmptyArray() throws Exception {
        Client client = ClientBuilder.newClient();
        EmptyArrayValidationFoo foo = new EmptyArrayValidationFoo(new Object[] {});
        Response response = client.target(generateURL("/emptyarray")).request()
                .post(Entity.entity(foo, MediaType.APPLICATION_JSON), Response.class);
        Object header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertTrue(header instanceof String, "Header has wrong format");
        Assertions.assertTrue(Boolean.valueOf(String.class.cast(header)), "Header has wrong format");
        String answer = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(answer));
        TestUtil.countViolations(e, 1, 0, 0, 1, 0);
    }
}
