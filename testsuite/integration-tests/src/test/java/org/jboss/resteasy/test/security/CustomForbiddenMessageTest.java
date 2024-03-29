package org.jboss.resteasy.test.security;

import java.util.Hashtable;

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
import org.jboss.resteasy.test.security.resource.CustomForbiddenMessageExceptionMapper;
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
 * @tpTestCaseDetails Custom ExceptionMapper is used when Forbidden is thrown from RoleBasedSecurityFilter
 * @tpSince RESTEasy 3.1.0
 */
@ServerSetup({ CustomForbiddenMessageTest.SecurityDomainSetup.class })
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CustomForbiddenMessageTest {

    private static ResteasyClient authorizedClient;

    private static final String ACCESS_FORBIDDEN_MESSAGE = "My custom message from CustomForbiddenMessageExceptionMapper: Access forbidden: role not allowed";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CustomForbiddenMessageTest.class.getSimpleName());

        Hashtable<String, String> contextParams = new Hashtable<String, String>();
        contextParams.put("resteasy.role.based.security", "true");

        war.addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web.xml", "/jboss-web.xml")
                .addAsWebInfResource(BasicAuthTest.class.getPackage(), "web.xml", "/web.xml");

        return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class,
                CustomForbiddenMessageExceptionMapper.class);
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

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomForbiddenMessageTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests custom message from custom ExceptionMapper
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testCustomExceptionMapper() throws Exception {
        Response response = authorizedClient.target(generateURL("/secured/deny")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        Assertions.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
        String ct = response.getHeaderString("Content-Type");
        Assertions.assertEquals("text/plain;charset=UTF-8", ct);
    }

    static class SecurityDomainSetup extends AbstractUsersRolesSecurityDomainSetup {

        SecurityDomainSetup() {
            super(CustomForbiddenMessageTest.class.getResource("users.properties"),
                    CustomForbiddenMessageTest.class.getResource("roles.properties"));
        }
    }
}
