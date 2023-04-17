package org.jboss.resteasy.embedded.test.interceptor;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.embedded.test.AbstractBootstrapTest;
import org.jboss.resteasy.embedded.test.TestApplication;
import org.jboss.resteasy.embedded.test.interceptor.resource.ClientRequestFilterImpl;
import org.jboss.resteasy.embedded.test.interceptor.resource.ClientResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Tests @Provider annotation on ClientRequestFilter
 * @tpSince RESTEasy 4.1.0
 */
public class ClientRequestFilterRegistrationTest extends AbstractBootstrapTest {

    @Before
    public void before() throws Exception {
        start(new TestApplication(ClientResource.class, ClientRequestFilterImpl.class));
    }

    @Test
    public void filterRegisteredTest() throws Exception {
        WebTarget base = client.target(generateURL("/") + "testIt");
        Response response = base.request().get();
        Assert.assertEquals(456, response.getStatus());
    }

}
