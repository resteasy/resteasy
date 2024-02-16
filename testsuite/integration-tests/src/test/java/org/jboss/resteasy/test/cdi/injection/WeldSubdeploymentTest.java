package org.jboss.resteasy.test.cdi.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.injection.resource.WeldSubdeploymentApplicationResource;
import org.jboss.resteasy.test.cdi.injection.resource.WeldSubdeploymentCdiJpaInjectingBean;
import org.jboss.resteasy.test.cdi.injection.resource.WeldSubdeploymentRequestResource;
import org.jboss.resteasy.test.cdi.injection.resource.WeldSubdeploymentStatefulResource;
import org.jboss.resteasy.test.cdi.injection.resource.WeldSubdeploymentStatelessResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-4084, WFLY-6485
 *                    Test that JPA dependencies are set for sub-deployments
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class WeldSubdeploymentTest {

    protected static final Logger logger = Logger.getLogger(WeldSubdeploymentTest.class.getName());

    private static final String WAR_DEPLOYMENT_NAME = "simple";

    public static final String ERROR_MESSAGE = "JBEAP-4084 regression, injected EntityManagerFactory should not be null but is";

    Client client;

    @Deployment
    public static Archive<?> deploy() {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "WeldSubdeploymentTest.ear");
        WebArchive war = ShrinkWrap.create(WebArchive.class, String.format("%s.war", WAR_DEPLOYMENT_NAME));
        war.addClasses(WeldSubdeploymentTest.class, WeldSubdeploymentCdiJpaInjectingBean.class, TestApplication.class);
        war.addClasses(WeldSubdeploymentRequestResource.class, WeldSubdeploymentApplicationResource.class,
                WeldSubdeploymentStatefulResource.class, WeldSubdeploymentStatelessResource.class);
        war.addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "util.jar");
        jar.addAsManifestResource(WeldSubdeploymentTest.class.getPackage(), "persistence_subdeployment.xml", "persistence.xml");
        war.addAsLibrary(jar);
        ear.addAsModule(war);
        return ear;
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
    }

    private void testEndPoint(String scope) {
        String url = PortProviderUtil.generateURL(String.format("/%s", scope), WAR_DEPLOYMENT_NAME);
        logger.info(String.format("Request to %s", url));
        WebTarget base = client.target(url);

        Response response = base.request().get();
        assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Test for bean with application scope
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAppcliationScope() throws Exception {
        testEndPoint("application");
    }

    /**
     * @tpTestDetails Test for bean with request scope
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestScope() throws Exception {
        testEndPoint("request");
    }

    /**
     * @tpTestDetails Test for stateful bean
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStateful() throws Exception {
        testEndPoint("stateful");
    }

    /**
     * @tpTestDetails Test for stateless bean
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStateLess() throws Exception {
        testEndPoint("stateless");
    }
}
