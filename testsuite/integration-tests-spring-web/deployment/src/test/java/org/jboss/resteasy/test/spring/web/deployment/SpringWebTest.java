package org.jboss.resteasy.test.spring.web.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.spring.web.deployment.resource.Greeting;
import org.jboss.resteasy.test.spring.web.deployment.resource.GreetingControllerWithNoRequestMapping;
import org.jboss.resteasy.test.spring.web.deployment.resource.ResponseEntityController;
import org.jboss.resteasy.test.spring.web.deployment.resource.ResponseStatusController;
import org.jboss.resteasy.test.spring.web.deployment.resource.SomeClass;
import org.jboss.resteasy.test.spring.web.deployment.resource.TestController;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@RunWith(Arquillian.class)
@RunAsClient
public class SpringWebTest {

    static Client client;
    private static final String DEPLOYMENT_NAME = "springdep";

    @Before
    public void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    @Test
    public void verifyGetWithQueryParam() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/hello?name=people");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "hello people", str);
    }

    @Test
    public void verifyGetToMethodWithoutForwardSlash() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/yolo");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "yolo", str);
    }

    @Test
    public void verifyGetUsingDefaultValue() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/hello2");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "hello world", str);
    }

    @Test
    public void verifyGetUsingNonEnglishChars() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/hello3?name=Γιώργος");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "hello Γιώργος", str);
    }

    @Test
    public void verifyPathWithWildcard() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/wildcard/whatever/world");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "world", str);
    }

    @Test
    public void verifyPathWithMultipleWildcards() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/wildcard2/something/folks/somethingelse");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "folks", str);
    }

    @Test
    public void verifyPathWithAntStyleWildCard() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/antwildcard/whatever/we/want");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "ant", str);
    }

    @Test
    public void verifyPathWithCharacterWildCard() {
        for (char c : new char[]{'t', 'r'}) {
            WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + String.format("/ca%cs", c));
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String str = response.readEntity(String.class);
            Assert.assertEquals("Unexpected response content from the server", "single", str);
        }
    }

    @Test
    public void verifyPathWithMultipleCharacterWildCards() {
        for (String path : new String[]{"/cars/shop/info", "/cart/show/info"}) {
            WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + path);
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String str = response.readEntity(String.class);
            Assert.assertEquals("Unexpected response content from the server", "multiple", str);
        }
    }

    @Test
    public void verifyPathVariableTypeConversion() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/int/9");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "10", str);
    }


    @Test
    public void verifyJsonGetWithPathParamAndGettingMapping() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/" + "json/dummy");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("dummy"));
    }

    @Test
    public void verifyJsonOnRequestMappingGetWithPathParamAndRequestMapping() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/" + "json2/dummy");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("dummy"));
    }

    @Test
    public void verifyJsonPostWithPostMapping() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/" + "json");
        Response response = target.request().post(Entity.entity("{\"message\": \"hi\"}", MediaType.APPLICATION_JSON));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("hi"));
    }

    @Test
    public void verifyJsonPostWithRequestMapping() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/" + "json2");
        Response response = target.request().post(Entity.entity("{\"message\": \"hi\"}", MediaType.APPLICATION_JSON));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("hi"));
    }

    @Test
    public void verifyMultipleInputAndJsonResponse() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/" + "json3?suffix=!");
        Response response = target.request().put(Entity.entity("{\"message\": \"hi\"}", MediaType.APPLICATION_JSON));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("hi!"));
    }

    @Test
    public void verifyHttpServletRequestParameterInjection() {
        WebTarget target = client.target(getBaseURL() + TestController.CONTROLLER_PATH + "/" + "servletRequest");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertTrue("Unexpected response content from the server", str.contains("localhost") || str.contains("127.0.0.1"));
    }

    @Test
    public void verifyEmptyContentResponseEntity() {
        WebTarget target = client.target(getBaseURL() + ResponseEntityController.CONTROLLER_PATH + "/noContent");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
    }

    @Test
    public void verifyStringContentResponseEntity() {
        WebTarget target = client.target(getBaseURL() + ResponseEntityController.CONTROLLER_PATH + "/string");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "hello world", str);
    }

    @Test
    public void verifyJsonContentResponseEntity() {
        WebTarget target = client.target(getBaseURL() + ResponseEntityController.CONTROLLER_PATH + "/" + "json");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        SomeClass someClass = response.readEntity(SomeClass.class);
        Assert.assertEquals("Unexpected response content from the server", someClass.getMessage(), "dummy");
        Assert.assertTrue("Incorrect headers response", response.getHeaderString("custom-header").contains("somevalue"));
    }

    @Test
    public void verifyJsonContentResponseEntityWithoutType() {
        WebTarget target = client.target(getBaseURL() + ResponseEntityController.CONTROLLER_PATH + "/" + "json2");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        SomeClass someClass = response.readEntity(SomeClass.class);
        Assert.assertEquals("Unexpected response content from the server", someClass.getMessage(), "dummy");
    }

    @Test
    public void verifyEmptyContentResponseStatus() {
        WebTarget target = client.target(getBaseURL() + ResponseStatusController.CONTROLLER_PATH + "/noContent");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }

    @Test
    public void verifyStringResponseStatus() {
        WebTarget target = client.target(getBaseURL() + ResponseStatusController.CONTROLLER_PATH + "/string");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_ACCEPTED, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "accepted", str);
    }

    @Test
    public void verifyControllerWithoutRequestMapping() {
        WebTarget target = client.target(getBaseURL() + "hello");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        Assert.assertEquals("Unexpected response content from the server", "hello world", str);
    }


    private String getBaseURL() {
        return PortProviderUtil.generateURL("/", DEPLOYMENT_NAME);
    }

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_NAME + ".war");
        archive.addAsWebInfResource(SpringWebTest.class.getPackage(), "web.xml", "web.xml");
        archive.addAsManifestResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml");


        TestUtilSpring.addSpringLibraries(archive);
        archive.as(ZipExporter.class).exportTo(new File("target", DEPLOYMENT_NAME + ".war"), true);
        return TestUtil.finishContainerPrepare(archive, null,
                SomeClass.class, Greeting.class, TestController.class, ResponseEntityController.class, ResponseStatusController.class, GreetingControllerWithNoRequestMapping.class);
    }

}
