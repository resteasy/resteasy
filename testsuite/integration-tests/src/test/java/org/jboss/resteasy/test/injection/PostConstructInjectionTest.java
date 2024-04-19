package org.jboss.resteasy.test.injection;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.test.injection.resource.PostConstructInjectionEJBInterceptor;
import org.jboss.resteasy.test.injection.resource.PostConstructInjectionEJBResource;
import org.jboss.resteasy.test.injection.resource.PostConstructInjectionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Validation and @PostConstruct methods
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2227
 * @tpSince RESTEasy 3.6
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PostConstructInjectionTest {

    static Client client;

    // deployment names
    private static final String WAR_CDI_ON = "war_with_cdi_on";
    private static final String WAR_CDI_OFF = "war_with_cdi_off";

    /**
     * Deployment with CDI activated
     */
    @Deployment(name = WAR_CDI_ON)
    public static Archive<?> deployCdiOn() {
        WebArchive war = TestUtil.prepareArchive(PostConstructInjectionTest.class.getSimpleName() + "_CDI_ON");
        war.addClass(PostConstructInjectionEJBInterceptor.class);
        war.addAsWebInfResource(PostConstructInjectionTest.class.getPackage(), "PostConstructInjection_beans_cdi_on.xml",
                "beans.xml");
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new HibernateValidatorPermission("accessPrivateMembers")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, PostConstructInjectionResource.class,
                PostConstructInjectionEJBResource.class);
    }

    /**
     * Deployment with CDI not activated
     */
    @Deployment(name = WAR_CDI_OFF)
    public static Archive<?> deployCdiOff() {
        WebArchive war = TestUtil.prepareArchive(PostConstructInjectionTest.class.getSimpleName() + "_CDI_OFF");
        war.addClass(PostConstructInjectionEJBInterceptor.class);
        war.addAsWebInfResource(PostConstructInjectionTest.class.getPackage(), "PostConstructInjection_beans_cdi_off.xml",
                "beans.xml");
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new HibernateValidatorPermission("accessPrivateMembers")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, PostConstructInjectionResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() {
        client.close();
    }

    private String generateURL(String jar, String path) {
        return PortProviderUtil.generateURL(path, PostConstructInjectionTest.class.getSimpleName() + "_CDI_" + jar);
    }

    /**
     * @tpTestDetails In an environment with managed beans, a @PostConstruct method on either an ordinary
     *                resource or an EJB interceptor should execute before class and property validation is done.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void TestPostInjectCdiOn() throws Exception {
        doTestPostInjectCdiOn("ON", "/normal");
    }

    /**
     * @tpTestDetails In an environment with managed beans, a @PostConstruct method on either an ordinary
     *                resource or an EJB interceptor should execute before class and property validation is done.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    @Disabled("This test doesn't work yet. See RESTEASY-2264")
    public void TestPostInjectCdiOnEJB() throws Exception {
        doTestPostInjectCdiOn("ON", "/ejb");
    }

    /**
     * @tpTestDetails In an environment without managed beans, a @PostConstruct method on a resource will not be called.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void TestPostInjectCdiOff() throws Exception {
        Response response = client.target(generateURL("OFF", "/normal/get")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ab", response.readEntity(String.class));
        response.close();
    }

    void doTestPostInjectCdiOn(String cdi, String resource) {
        Response response = client.target(generateURL(cdi, resource + "/get")).request().get();
        Assertions.assertEquals(400, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header);
        ViolationReport report = response.readEntity(ViolationReport.class);
        Assertions.assertEquals(1, report.getPropertyViolations().size());
        response.close();
    }
}
