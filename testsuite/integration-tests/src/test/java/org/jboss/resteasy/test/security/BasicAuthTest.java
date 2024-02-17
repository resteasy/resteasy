package org.jboss.resteasy.test.security;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClientEngine;
import org.jboss.resteasy.setup.AbstractUsersRolesSecurityDomainSetup;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.annotations.RequiresModule;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseProxy;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResource;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResourceAnybody;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResourceMoreSecured;
import org.jboss.resteasy.test.security.resource.BasicAuthRequestFilter;
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
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic test for RESTEasy authentication.
 * @tpSince RESTEasy 3.0.16
 */
@ServerSetup({ BasicAuthTest.SecurityDomainSetup.class })
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class BasicAuthTest {

    private static final String WRONG_RESPONSE = "Wrong response content.";
    private static final String ACCESS_FORBIDDEN_MESSAGE = "Access forbidden: role not allowed";

    private static ResteasyClient authorizedClient;
    private static ResteasyClient unauthorizedClient;
    private static ResteasyClient noAutorizationClient;

    // Following clients are used in tests for ClientRequestFilter
    private static ResteasyClient authorizedClientUsingRequestFilter;
    private static ResteasyClient unauthorizedClientUsingRequestFilter;
    private static ResteasyClient unauthorizedClientUsingRequestFilterWithWrongPassword;

    @BeforeAll
    public static void init() {
        // authorizedClient
        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password1");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
            CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(client);
            authorizedClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        }
        // unauthorizedClient
        {
            UsernamePasswordCredentials credentials_other = new UsernamePasswordCredentials("ordinaryUser", "password2");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials_other);
            CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(client);
            unauthorizedClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        }
        // noAuthorizationClient
        noAutorizationClient = (ResteasyClient) ClientBuilder.newClient();

        // authorizedClient with ClientRequestFilter
        {
            ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
            authorizedClientUsingRequestFilter = (ResteasyClient) builder
                    .register(new BasicAuthRequestFilter("bill", "password1")).build();
        }
        // unauthorizedClient with ClientRequestFilter - unauthorized user
        {
            ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
            unauthorizedClientUsingRequestFilter = (ResteasyClient) builder
                    .register(new BasicAuthRequestFilter("ordinaryUser", "password2")).build();
        }
        // unauthorizedClient with ClientRequestFilter - wrong password
        {
            ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
            unauthorizedClientUsingRequestFilterWithWrongPassword = (ResteasyClient) builder
                    .register(new BasicAuthRequestFilter("bill", "password2")).build();
        }
    }

    @AfterAll
    public static void after() throws Exception {
        authorizedClient.close();
        unauthorizedClient.close();
        noAutorizationClient.close();
        authorizedClientUsingRequestFilter.close();
        unauthorizedClientUsingRequestFilter.close();
        unauthorizedClientUsingRequestFilterWithWrongPassword.close();
    }

    @Deployment
    public static Archive<?> deployLocatingResource() {
        WebArchive war = TestUtil.prepareArchive(BasicAuthTest.class.getSimpleName());

        Hashtable<String, String> contextParams = new Hashtable<String, String>();
        contextParams.put("resteasy.role.based.security", "true");

        war.addClass(BasicAuthBaseProxy.class)
                .addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web.xml", "/jboss-web.xml")
                .addAsWebInfResource(BasicAuthTest.class.getPackage(), "web.xml", "/web.xml");

        return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class,
                BasicAuthBaseResourceMoreSecured.class, BasicAuthBaseResourceAnybody.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, BasicAuthTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Basic ProxyFactory test. Correct credentials are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        BasicAuthBaseProxy proxy = authorizedClient.target(generateURL("/")).proxyBuilder(BasicAuthBaseProxy.class).build();
        Assertions.assertEquals(proxy.get(), "hello", WRONG_RESPONSE);
        Assertions.assertEquals(proxy.getAuthorized(), "authorized", WRONG_RESPONSE);
    }

    /**
     * @tpTestDetails Basic ProxyFactory test. No credentials are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxyFailure() throws Exception {
        BasicAuthBaseProxy proxy = noAutorizationClient.target(generateURL("/")).proxyBuilder(BasicAuthBaseProxy.class).build();
        try {
            proxy.getFailure();
            Assertions.fail();
        } catch (NotAuthorizedException e) {
            Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, e.getResponse().getStatus());
            Assertions.assertTrue(e.getResponse().getHeaderString("WWW-Authenticate").contains("Basic realm="),
                    "WWW-Authenticate header is not included");
        }
    }

    /**
     * @tpTestDetails Test secured resource with correct and incorrect credentials.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurity() throws Exception {
        // authorized client
        {
            Response response = authorizedClient.target(generateURL("/secured")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("hello", response.readEntity(String.class), WRONG_RESPONSE);
        }

        {
            Response response = authorizedClient.target(generateURL("/secured/authorized")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("authorized", response.readEntity(String.class), WRONG_RESPONSE);
        }

        {
            Response response = authorizedClient.target(generateURL("/secured/deny")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
            Assertions.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class), WRONG_RESPONSE);
        }
        {
            Response response = authorizedClient.target(generateURL("/secured3/authorized")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("authorized", response.readEntity(String.class), WRONG_RESPONSE);
        }

        // unauthorized client
        {
            Response response = unauthorizedClient.target(generateURL("/secured3/authorized")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
            Assertions.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class), WRONG_RESPONSE);
        }
        {
            Response response = unauthorizedClient.target(generateURL("/secured3/anybody")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-579
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test579() throws Exception {
        Response response = authorizedClient.target(generateURL("/secured2")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check failures for secured resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFailure() throws Exception {
        {
            Response response = noAutorizationClient.target(generateURL("/secured")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
            Assertions.assertTrue(response.getHeaderString("WWW-Authenticate").contains("Basic realm="),
                    "WWW-Authenticate header is not included");
            response.close();
        }

        {
            Response response = authorizedClient.target(generateURL("/secured/authorized")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("authorized", response.readEntity(String.class), WRONG_RESPONSE);
        }

        {
            Response response = unauthorizedClient.target(generateURL("/secured/authorized")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
            Assertions.assertEquals(response.readEntity(String.class), ACCESS_FORBIDDEN_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Regression test for JBEAP-1589, RESTEASY-1249
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAccesForbiddenMessage() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password1");
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
        CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(client);

        ResteasyClient authorizedClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        Response response = authorizedClient.target(generateURL("/secured/deny")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        Assertions.assertEquals(response.readEntity(String.class), ACCESS_FORBIDDEN_MESSAGE);
        authorizedClient.close();
    }

    /**
     * @tpTestDetails Test Content-type when forbidden exception is raised, RESTEASY-1563
     * @tpSince RESTEasy 3.1.1
     */
    @Test
    @RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "6.2.8.Final", issueId = "RESTEASY-3205")
    public void testContentTypeWithForbiddenMessage() {
        Response response = unauthorizedClient.target(generateURL("/secured/denyWithContentType")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", response.getHeaderString("Content-type"),
                "Incorrect Content-type header");
        Assertions.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class),
                "Missing forbidden message in the response");
    }

    /**
     * @tpTestDetails Test Content-type when unauthorized exception is raised
     * @tpSince RESTEasy 3.1.1
     */
    @Test
    public void testContentTypeWithUnauthorizedMessage() {
        Response response = noAutorizationClient.target(generateURL("/secured/denyWithContentType")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
        Assertions.assertEquals("text/html;charset=UTF-8", response.getHeaderString("Content-type"),
                "Incorrect Content-type header");
        Assertions.assertTrue(response.getHeaderString("WWW-Authenticate").contains("Basic realm="),
                "WWW-Authenticate header is not included");
    }

    /**
     * @tpTestDetails Test secured resource with correct credentials. Authentication is done using BasicAuthRequestFilter.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testWithClientRequestFilterAuthorizedUser() {
        Response response = authorizedClientUsingRequestFilter.target(generateURL("/secured/authorized")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("authorized", response.readEntity(String.class), WRONG_RESPONSE);
    }

    /**
     * @tpTestDetails Test secured resource with incorrect credentials. Authentication is done using BasicAuthRequestFilter.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testWithClientRequestFilterWrongPassword() {
        Response response = unauthorizedClientUsingRequestFilterWithWrongPassword.target(generateURL("/secured/authorized"))
                .request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, response.getStatus());
        Assertions.assertTrue(response.getHeaderString("WWW-Authenticate").contains("Basic realm="),
                "WWW-Authenticate header is not included");
    }

    /**
     * @tpTestDetails Test secured resource with correct credentials of user that is not authorized to the resource.
     *                Authentication is done using BasicAuthRequestFilter.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testWithClientRequestFilterUnauthorizedUser() {
        Response response = unauthorizedClientUsingRequestFilter.target(generateURL("/secured/authorized")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        Assertions.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class),
                WRONG_RESPONSE);
    }

    /**
     * @tpTestDetails Test that client correctly loads ClientConfigProvider implementation and uses credentials when making a
     *                request.
     *                Also test these credentials are ignored if different are set.
     */
    @Test
    public void testClientConfigProviderCredentials() throws IOException {
        String jarPath = ClientConfigProviderTestJarHelper.createClientConfigProviderTestJarWithBASIC();

        Process process = ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_CREDENTIALS_ARE_USED_FOR_BASIC,
                jarPath,
                new String[] { generateURL("/secured/authorized") });
        String line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assertions.assertEquals("200", line);
        process.destroy();

        process = ClientConfigProviderTestJarHelper.runClientConfigProviderTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_CLIENTCONFIG_CREDENTIALS_ARE_IGNORED_IF_DIFFERENT_SET,
                jarPath,
                new String[] { generateURL("/secured/authorized") });
        line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assertions.assertEquals("401", line);
        process.destroy();

        Assertions.assertTrue(new File(jarPath).delete());
    }

    static class SecurityDomainSetup extends AbstractUsersRolesSecurityDomainSetup {

        SecurityDomainSetup() {
            super(BasicAuthTest.class.getResource("users.properties"), BasicAuthTest.class.getResource("roles.properties"));
        }

    }
}
