package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.resource.param.resource.ParamInterfaceResource;
import org.jboss.resteasy.test.resource.param.resource.ParamResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-423 and RESTEASY-522
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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


    /**
     * @tpTestDetails Null matrix parameters should be accepted by the reasteasy client library (RESTEASY-423)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullMatrixParam() throws Exception {
        ParamInterfaceResource proxy = ProxyFactory.create(ParamInterfaceResource.class, generateBaseUrl());
        String rtn = proxy.getMatrix(null);
        Assert.assertEquals("null", rtn);
    }

    /**
     * @tpTestDetails RestEasy Client Framework should not throw null point exception when
     *                the @CookieParam() is null (RESTEASY-522)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullCookieParam() throws Exception {
        ParamInterfaceResource proxy = ProxyFactory.create(ParamInterfaceResource.class, generateBaseUrl());
        String rtn = proxy.getCookie(null);
        Assert.assertEquals("null", rtn);
    }

    /**
     * @tpTestDetails RestEasy Client Framework should not throw null point exception when
     *                the @HeaderParam() is null (RESTEASY-522)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullHeaderParam() throws Exception {
        ParamInterfaceResource proxy = ProxyFactory.create(ParamInterfaceResource.class, generateBaseUrl());
        String rtn = proxy.getHeader(null);
        Assert.assertEquals("null", rtn);
    }

}
