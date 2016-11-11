package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.InheritanceAnimal;
import org.jboss.resteasy.test.providers.jaxb.resource.InheritanceZoo;
import org.jboss.resteasy.test.providers.jaxb.resource.InheritanceCat;
import org.jboss.resteasy.test.providers.jaxb.resource.InheritanceDog;
import org.jboss.resteasy.test.providers.jaxb.resource.InheritanceResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InheritanceTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InheritanceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, InheritanceAnimal.class, InheritanceCat.class, InheritanceDog.class,
                InheritanceZoo.class, InheritanceResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InheritanceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests Jaxb object with inheritance structure
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInheritance() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/zoo"));
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        InheritanceZoo zoo = response.readEntity(InheritanceZoo.class);
        Assert.assertEquals("The number of animals in the zoo doesn't match the expected count", 2, zoo.getAnimals().size());
    }

}
