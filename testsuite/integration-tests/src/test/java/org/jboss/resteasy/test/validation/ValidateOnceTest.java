package org.jboss.resteasy.test.validation;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationCounter;
import org.jboss.resteasy.test.validation.resource.ValidationCounterConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCounterResource;
import org.jboss.resteasy.test.validation.resource.ValidationCounterValidator;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Validator provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails RESTEASY-3339: Assert that parameter validation is executed only once
 * @tpSince RESTEasy 7.0.2
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ValidateOnceTest {

    Client client;

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment(testable = false)
    public static Archive<?> basicDeploymentTestResourceWithOtherGroups() {
        WebArchive war = TestUtil.prepareArchive(ValidateOnceTest.class.getSimpleName());
        war.addClasses(ValidationCounterResource.class, ValidationCounter.class,
                ValidationCounterConstraint.class, ValidationCounterValidator.class);
        return TestUtil.finishContainerPrepare(war, null);
    }

    /**
     * @tpTestDetails Assert that parameter validation is executed only once
     * @tpSince RESTEasy 7.0.2
     */
    @Test
    public void testParameters() throws Exception {
        Response response = client
                .target(PortProviderUtil.generateURL("/count", ValidateOnceTest.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationCounter(), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }
}
