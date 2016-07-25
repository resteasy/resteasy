package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

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

        ResteasyClient client = new ResteasyClientBuilder().build();
        ClientWebTarget clientWebTarget = (ClientWebTarget) client.target("");

        Assert.assertTrue("Client properties should not be empty", client.getConfiguration().getProperties().isEmpty());

        clientWebTarget.property(property, property);

        Assert.assertEquals("Add of property faild", Collections.singletonMap(property, property), clientWebTarget.getConfiguration().getProperties());

        try {
            clientWebTarget.property(property, null);
        } catch (NullPointerException ex) {
            Assert.fail("Cannot remove property with null value.");
        }

        Object value = clientWebTarget.getConfiguration().getProperty(property);
        Assert.assertNull("Property from webTarget can not be removed", value);
    }
}
