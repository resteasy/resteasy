package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorBaseCrudService;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorBaseService;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorFoo;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorImpFoo;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorOhaUserModel;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorPlatformServiceImpl;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorPlatformServiceResource;
import org.jboss.resteasy.test.resource.basic.resource.SubResourceLocatorUserResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-657
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SubResourceLocatorTest {

    @Deployment
    public static Archive<?> testReturnValuesDeploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(SubResourceLocatorTest.class.getSimpleName());
        war.addClasses(SubResourceLocatorBaseCrudService.class, SubResourceLocatorBaseService.class,
                SubResourceLocatorFoo.class, SubResourceLocatorOhaUserModel.class,
                SubResourceLocatorPlatformServiceResource.class, SubResourceLocatorUserResource.class);
        return TestUtil.finishContainerPrepare(war, null, SubResourceLocatorImpFoo.class,
                SubResourceLocatorPlatformServiceImpl.class);
    }

    /**
     * @tpTestDetails Sub resource locator should not fail
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test657() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget base = client.target(PortProviderUtil.generateURL("/platform/users/89080/data/ada/jsanchez110",
                SubResourceLocatorTest.class.getSimpleName()));

        Response response = base.request().get();
        String s = response.readEntity(String.class);
        Assert.assertThat("Wrong response content", s, is("bill"));
        response.close();
        client.close();
    }
}
