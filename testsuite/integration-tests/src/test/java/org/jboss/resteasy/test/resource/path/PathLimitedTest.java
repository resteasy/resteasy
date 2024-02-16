package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedBasicResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedLocatorResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedLocatorUriResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedUnlimitedOnPathResource;
import org.jboss.resteasy.test.resource.path.resource.PathLimitedUnlimitedResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for limited and unlimited path
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PathLimitedTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PathLimitedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, PathLimitedUnlimitedOnPathResource.class,
                PathLimitedUnlimitedResource.class,
                PathLimitedLocatorResource.class, PathLimitedLocatorUriResource.class, PathLimitedBasicResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private void basicTest(String path) {
        Response response = client.target(PortProviderUtil.generateURL(path, PathLimitedTest.class.getSimpleName())).request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
