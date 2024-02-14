package org.jboss.resteasy.test.cdi.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonLocalIF;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonRootResource;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonSubResource;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonTestBean;
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
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for Singleton beans
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SingletonTest {
    static Client client;
    protected static final Logger logger = Logger.getLogger(SingletonTest.class.getName());

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SingletonTest.class.getSimpleName());
        war.addClasses(SingletonLocalIF.class, SingletonSubResource.class,
                SingletonTestBean.class);
        return TestUtil.finishContainerPrepare(war, null, SingletonRootResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SingletonTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Three requests for singleton bean
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSingleton() throws Exception {
        WebTarget base = client.target(generateURL("/root"));
        String value = base.path("sub").request().get(String.class);
        Assertions.assertEquals("hello", value, "Wrong content of response");
        value = base.path("injected").request().get(String.class);
        Assertions.assertEquals("true", value, "Wrong content of response");
        value = base.path("intfsub").request().get(String.class);
        logger.info(value);
        Response response = base.path("exception").request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());
    }

}
