package org.jboss.resteasy.test.security;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.setup.UsersRolesSecurityDomainSetupCreaper;
import org.jboss.resteasy.test.security.resource.BasicAuthBaseResource;
import org.jboss.resteasy.test.security.resource.CustomForbiddenMessageExceptionMapper;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.util.Hashtable;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Custom ExceptionMapper is used when Forbidden is thrown from RoleBasedSecurityFilter, see RESTEASY-1342
 * @tpSince RESTEasy 3.0.21.Final
 */
@ServerSetup({UsersRolesSecurityDomainSetupCreaper.class})
@RunWith(Arquillian.class)
@RunAsClient
@Category({NotForForwardCompatibility.class})
public class CustomForbiddenMessageTest {

    private static ResteasyClient authorizedClient;

    private static final String ACCESS_FORBIDDEN_MESSAGE = "My custom message from CustomForbiddenMessageExceptionMapper: Access forbidden: role not allowed";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CustomForbiddenMessageTest.class.getSimpleName());

        Hashtable<String, String> contextParams = new Hashtable<String, String>();
        contextParams.put("resteasy.role.based.security", "true");

        war.addAsResource(BasicAuthTest.class.getPackage(), "roles.properties", "/roles.properties")
                .addAsResource(BasicAuthTest.class.getPackage(), "users.properties", "/users.properties")
                .addAsWebInfResource(BasicAuthTest.class.getPackage(), "jboss-web.xml", "/jboss-web.xml")
                .addAsWebInfResource(BasicAuthTest.class.getPackage(), "web.xml", "/web.xml");

        return TestUtil.finishContainerPrepare(war, contextParams, BasicAuthBaseResource.class, CustomForbiddenMessageExceptionMapper.class);
    }

    @BeforeClass
    public static void init() {
        // authorizedClient
        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password1");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), credentials);
            CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
            ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(client);
            authorizedClient = new ResteasyClientBuilder().httpEngine(engine).build();
        }
    }

    @AfterClass
    public static void after() throws Exception {
        authorizedClient.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomForbiddenMessageTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests custom message from custom ExceptionMapper
     * @tpSince RESTEasy 3.0.21.Final
     */
    @Test
    public void testCustomExceptionMapper() throws Exception {
        Response response = authorizedClient.target(generateURL("/secured/deny")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        Assert.assertEquals(ACCESS_FORBIDDEN_MESSAGE, response.readEntity(String.class));
    }
}
