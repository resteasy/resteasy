package org.jboss.resteasy.test.security;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Two different security domains in two deployments. Both domains are by default created in Elytron.
 * @tpSince RESTEasy 3.0.21
 */
@ServerSetup(TwoSecurityDomainsTest.SecurityDomainSetup.class)
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@Tag("ExpectedFailingOnWildFly18.class") //WFLY-12655
public class TwoSecurityDomainsTest {

    private static ResteasyClient authorizedClient;
    private static final String SECURITY_DOMAIN_DEPLOYMENT_1 = "jaxrsSecDomain";
    private static final String SECURITY_DOMAIN_DEPLOYMENT_2 = "jaxrsSecDomain2";
    private static final String WRONG_RESPONSE = "Wrong response content.";

    @Deployment(name = "SECURITY_DOMAIN_DEPLOYMENT_1")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_1);

        Hashtable<String, String> contextParams = new Hashtable<String, String>();
        contextParams.put("resteasy.role.based.security", "true");

        war.addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web.xml", "/jboss-web.xml")
                .addAsWebInfResource(TwoSecurityDomainsTest.class.getPackage(), "web.xml", "/web.xml");

        return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class);
    }

    @Deployment(name = "SECURITY_DOMAIN_DEPLOYMENT_2")
    public static Archive<?> deploy2() {
        WebArchive war = TestUtil.prepareArchive(TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_2);

        Hashtable<String, String> contextParams = new Hashtable<String, String>();
        contextParams.put("resteasy.role.based.security", "true");

        war.addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web2.xml", "/jboss-web.xml")
                .addAsWebInfResource(TwoSecurityDomainsTest.class.getPackage(), "web.xml", "/web.xml");

        return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class);
    }

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
    }

    @AfterAll
    public static void after() throws Exception {
        authorizedClient.close();
    }

    /**
     * @tpTestDetails Client using correct authorization credentials sends GET request to the first and then second deployment
     * @tpSince RESTEasy 3.0.21
     */
    @Test
    public void testOneClientTwoDeploymentsTwoSecurityDomains() throws Exception {
        Response response = authorizedClient.target(PortProviderUtil.generateURL("/secured",
                TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_1)).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class), WRONG_RESPONSE);

        response = authorizedClient.target(PortProviderUtil.generateURL("/secured",
                TwoSecurityDomainsTest.class.getSimpleName() + SECURITY_DOMAIN_DEPLOYMENT_2)).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class), WRONG_RESPONSE);
    }

    static class SecurityDomainSetup extends AbstractUsersRolesSecurityDomainSetup {

        SecurityDomainSetup() {
            super(TwoSecurityDomainsTest.class.getResource("users.properties"),
                    TwoSecurityDomainsTest.class.getResource("roles.properties"));
        }

        public Map<String, String> getSecurityDomainConfig() {
            final Map<String, String> config = new HashMap<>();
            config.put(SECURITY_DOMAIN_DEPLOYMENT_1, "realm1");
            config.put(SECURITY_DOMAIN_DEPLOYMENT_2, "realm2");
            return config;
        }
    }
}
