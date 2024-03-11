package org.jboss.resteasy.test.client;

import java.util.Collections;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails Regression test for JBEAP-4708
 * @tpSince RESTEasy 3.0.17
 */
public class ClientWebTargetTest {

    /**
     * @tpTestDetails Test for removing property from WebTarget.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void propertyNullTest() throws Exception {
        String property = "property";

        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ClientWebTarget clientWebTarget = (ClientWebTarget) client.target("");

        Assertions.assertTrue(client.getConfiguration().getProperties().isEmpty(),
                "Client properties should not be empty");

        clientWebTarget.property(property, property);

        Assertions.assertEquals(Collections.singletonMap(property, property),
                clientWebTarget.getConfiguration().getProperties(),
                "Add of property faild");

        try {
            clientWebTarget.property(property, null);
        } catch (NullPointerException ex) {
            Assertions.fail("Cannot remove property with null value.");
        }

        Object value = clientWebTarget.getConfiguration().getProperty(property);
        Assertions.assertNull(value, "Property from webTarget can not be removed");
    }
}
