package org.jboss.resteasy.test.resource.constructor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.resource.constructor.resource.HappinessParams;
import org.jboss.resteasy.test.resource.constructor.resource.PrivateNoArgConstructorResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

@RunWith(Arquillian.class)
@RunAsClient
public class PrivateNoArgConstructorTest {
    protected static final Logger logger = Logger.getLogger(
            PrivateNoArgConstructorTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(
                PrivateNoArgConstructorTest.class.getSimpleName());
        war.addClass(PrivateNoArgConstructorResource.class);
        war.addClass(HappinessParams.class);
        return TestUtil.finishContainerPrepare(war, null);
    }

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path,
                PrivateNoArgConstructorTest.class.getSimpleName());
    }

    @Test
    public void constructorTest() {
        Response response = client.target(generateURL("/happiness"))
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 200,
                response.getStatus());
    }
}
