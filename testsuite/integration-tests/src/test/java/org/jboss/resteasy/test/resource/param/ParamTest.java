package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.resource.param.resource.ParamInterfaceResource;
import org.jboss.resteasy.test.resource.param.resource.ParamResource;
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
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-423 and RESTEASY-522
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ParamTest {

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(ParamTest.class.getSimpleName());
        war.addClass(ParamInterfaceResource.class);
        return TestUtil.finishContainerPrepare(war, null, ParamResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ParamTest.class.getSimpleName());
    }

    protected Client client;

    @BeforeEach
    public void beforeTest() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void afterTest() {
        client.close();
    }

    /**
     * @tpTestDetails Null matrix parameters should be accepted by the reasteasy client library (RESTEASY-423)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullMatrixParam() throws Exception {
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateBaseUrl());
        ParamInterfaceResource proxy = target.proxy(ParamInterfaceResource.class);
        String rtn = proxy.getMatrix(null);
        Assertions.assertEquals("null", rtn);
    }

    /**
     * @tpTestDetails RestEasy Client Framework should not throw null point exception when
     *                the @CookieParam() is null (RESTEASY-522)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullCookieParam() throws Exception {
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateBaseUrl());
        ParamInterfaceResource proxy = target.proxy(ParamInterfaceResource.class);
        String rtn = proxy.getCookie(null);
        Assertions.assertEquals("null", rtn);
    }

    /**
     * @tpTestDetails RestEasy Client Framework should not throw null point exception when
     *                the @HeaderParam() is null (RESTEASY-522)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullHeaderParam() throws Exception {
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateBaseUrl());
        ParamInterfaceResource proxy = target.proxy(ParamInterfaceResource.class);
        String rtn = proxy.getHeader(null);
        Assertions.assertEquals("null", rtn);
    }

}
