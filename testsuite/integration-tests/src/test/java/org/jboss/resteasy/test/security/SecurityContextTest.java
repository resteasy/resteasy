package org.jboss.resteasy.test.security;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;
import org.jboss.resteasy.setup.AbstractUsersRolesSecurityDomainSetup;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.security.resource.SecurityContextContainerRequestFilter;
import org.jboss.resteasy.test.security.resource.SecurityContextResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic test for RESTEasy authentication using programmatic security with jakarta.ws.rs.core.SecurityContext
 * @tpSince RESTEasy 3.0.16
 */
@ServerSetup({ SecurityContextTest.SecurityDomainSetup.class })
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@Tag("NotForBootableJar")
public class SecurityContextTest {

    private static final String USERNAME = "bill";
    private static final String PASSWORD = "password1";

    private static final String USERNAME2 = "ordinaryUser";
    private static final String PASSWORD2 = "password2";

    private Client authorizedClient;
    private Client nonauthorizedClient;

    @BeforeEach
    public void initClient() {

        // Create jaxrs client
        nonauthorizedClient = ClientBuilder.newClient();
        nonauthorizedClient.register(new BasicAuthentication(USERNAME2, PASSWORD2));

        // Create jaxrs client
        authorizedClient = ClientBuilder.newClient();
        authorizedClient.register(new BasicAuthentication(USERNAME, PASSWORD));

    }

    @AfterEach
    public void after() throws Exception {
        authorizedClient.close();
        nonauthorizedClient.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SecurityContextTest.class.getSimpleName());
        war.addAsWebInfResource(SecurityContextTest.class.getPackage(), "jboss-web.xml", "jboss-web.xml")
                .addAsWebInfResource(SecurityContextTest.class.getPackage(), "securityContext/web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, SecurityContextResource.class);
    }

    @Deployment(name = "containerRequestFilter")
    public static Archive<?> deploy2() {
        WebArchive war = TestUtil.prepareArchive(SecurityContextTest.class.getSimpleName() + "Filter");
        war.addAsWebInfResource(SecurityContextTest.class.getPackage(), "jboss-web.xml", "jboss-web.xml")
                .addAsWebInfResource(SecurityContextTest.class.getPackage(), "securityContext/web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, SecurityContextResource.class,
                SecurityContextContainerRequestFilter.class);
    }

    /**
     * @tpTestDetails Correct credentials are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityContextAuthorized() {
        Response response = authorizedClient
                .target(PortProviderUtil.generateURL("/test", SecurityContextTest.class.getSimpleName())).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Good user bill", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Incorrect credentials are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityContextNonAuthorized() {
        Response response = nonauthorizedClient
                .target(PortProviderUtil.generateURL("/test", SecurityContextTest.class.getSimpleName())).request().get();
        Assertions.assertEquals("User ordinaryUser is not authorized", response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
    }

    /**
     * @tpTestDetails ContainerRequestFilter and correct credentials are used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("containerRequestFilter")
    public void testSecurityContextAuthorizedUsingFilter() {
        Response response = authorizedClient
                .target(PortProviderUtil.generateURL("/test", SecurityContextTest.class.getSimpleName() + "Filter")).request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Good user bill", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails ContainerRequestFilter and incorrect credentials are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("containerRequestFilter")
    public void testSecurityContextNonAuthorizedUsingFilter() {
        Response response = nonauthorizedClient
                .target(PortProviderUtil.generateURL("/test", SecurityContextTest.class.getSimpleName() + "Filter")).request()
                .get();
        Assertions.assertEquals("User ordinaryUser is not authorized, coming from filter", response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
    }

    static class SecurityDomainSetup extends AbstractUsersRolesSecurityDomainSetup {

        SecurityDomainSetup() {
            super(SecurityContextTest.class.getResource("users.properties"),
                    SecurityContextTest.class.getResource("roles.properties"));
        }
    }
}
