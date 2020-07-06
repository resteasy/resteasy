package org.jboss.resteasy.test.microprofile.restclient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.microprofile.client.RestClientBuilderImpl;
import org.jboss.resteasy.test.microprofile.restclient.resource.JsonBindingMPService;
import org.jboss.resteasy.test.microprofile.restclient.resource.JsonBindingMPServiceIntf;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Dog;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

/**
 * @tpSubChapter MicroProfile rest client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Show JSON-Binding is supported.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonBindingMPTest {
    protected static final Logger LOG = Logger.getLogger(JsonBindingMPTest.class.getName());
    private static final String WAR_SERVICE = "jsonBinding_service";

    @Deployment(name=WAR_SERVICE)
    public static Archive<?> serviceDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(JsonBindingMPService.class,
                Dog.class);
       return TestUtil.finishContainerPrepare(war, null, null);
    }

    private static String generateURL(String path, String deployName) {
        return PortProviderUtil.generateURL(path, deployName);
    }

    @Test
    public void testDog() {
        RestClientBuilderImpl builder = new RestClientBuilderImpl();
        JsonBindingMPServiceIntf jsonBindingMPServiceIntf = builder
                .baseUri(URI.create(generateURL("", WAR_SERVICE)))
                .build(JsonBindingMPServiceIntf.class);

        try {
            Dog dog = new Dog("Rex", "german shepherd");
            Dog response = jsonBindingMPServiceIntf.getDog(dog);
            Assert.assertTrue(response.getName().equals("Jethro"));
            Assert.assertTrue(response.getSort().equals("stafford"));
        } catch (Exception e) {
           Assert.fail("Exception thrown: " + e);
        }
    }
}
