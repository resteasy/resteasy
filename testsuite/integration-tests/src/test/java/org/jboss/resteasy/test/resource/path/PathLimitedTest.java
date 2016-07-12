package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedBasicResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedLocatorResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedLocatorUriResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedUnlimitedOnPathResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedUnlimitedResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for limited and unlimited path
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PathLimitedTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PathLimitedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, PathLimitedUnlimitedOnPathResource.class, PathLimitedUnlimitedResource.class,
                PathLimitedLocatorResource.class, PathLimitedLocatorUriResource.class, PathLimitedBasicResource.class);
    }

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    private void basicTest(String path) {
        Response response = client.target(PortProviderUtil.generateURL(path, PathLimitedTest.class.getSimpleName())).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check unlimited behaviour on class
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUnlimitedOnClass() {
        basicTest("/unlimited");
        basicTest("/unlimited/on/and/on");
    }

    /**
     * @tpTestDetails Check unlimited behaviour on method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUnlimitedOnMethod() {
        basicTest("/unlimited2/on/and/on");
        basicTest("/unlimited2/runtime/org.jbpm:HR:1.0/process/hiring/start");
        basicTest("/uriparam/on/and/on?expected=on%2Fand%2Fon");
    }

    /**
     * @tpTestDetails Check location of resources
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocator() {
        basicTest("/locator");
        basicTest("/locator/on/and/on");
        basicTest("/locator2/on/and/on?expected=on%2Fand%2Fon");
        basicTest("/locator3/unlimited/unlimited2/on/and/on");
        basicTest("/locator3/unlimited/uriparam/on/and/on?expected=on%2Fand%2Fon");
        basicTest("/locator3/uriparam/1/uriparam/on/and/on?firstExpected=1&expected=on%2Fand%2Fon");

    }

}
