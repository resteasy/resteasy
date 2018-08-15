package org.jboss.resteasy.test.spring.deployment;

import javax.json.JsonArray;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.spring.deployment.resource.Greeting;
import org.jboss.resteasy.test.spring.deployment.resource.GreetingController;
import org.jboss.resteasy.test.spring.deployment.resource.JaxrsApplication;
import org.jboss.resteasy.test.spring.deployment.resource.NumbersResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.management.MBeanPermission;
import javax.management.MBeanServerPermission;
import javax.management.MBeanTrustPermission;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

/**
 * Test that springframework MVC works in conjuction with jaxrs Application subclass.
 * It's all about having the proper configuration in the web.xml.
 * User: rsearls
 * Date: 2/20/17
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JaxrsWithSpringMVCTest {

    static ResteasyClient client;

    @Deployment
    private static Archive<?> deploy() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, JaxrsWithSpringMVCTest.class.getSimpleName() + ".war")
            .addAsWebInfResource(JaxrsWithSpringMVCTest.class.getPackage(), "jaxrsWithSpringMVC/web.xml", "web.xml");
        archive.addAsWebInfResource(JaxrsWithSpringMVCTest.class.getPackage(),
            "jaxrsWithSpringMVC/spring-servlet.xml", "spring-servlet.xml");
        archive.addClass(GreetingController.class);
        archive.addClass(Greeting.class);
        archive.addClass(NumbersResource.class);
        archive.addClass(JaxrsApplication.class);

        // spring specific permissions needed.
        // Permission  accessClassInPackage.sun.reflect.annotation is required in order
        // for spring to introspect annotations.  Security exception is eaten by spring
        // and not posted via the server.
        archive.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new MBeanServerPermission("createMBeanServer"),
            new MBeanPermission("org.springframework.context.support.LiveBeansView#-[liveBeansView:application=/JaxrsWithSpringMVCTest]", "registerMBean,unregisterMBean"),
            new MBeanTrustPermission("register"),
            new PropertyPermission("spring.liveBeansView.mbeanDomain", "read"),
            new RuntimePermission("getenv.spring.liveBeansView.mbeanDomain"),
            new ReflectPermission("suppressAccessChecks"),
            new RuntimePermission("accessDeclaredMembers"),
            new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
            new FilePermission("<<ALL FILES>>", "read"),
            new LoggingPermission("control", "")
        ), "permissions.xml");

        TestUtilSpring.addSpringLibraries(archive);
        return archive;
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxrsWithSpringMVCTest.class.getSimpleName());
    }

    /**
     */
    @Test
    public void testAllEndpoints() throws Exception {

        {
            WebTarget target = client.target(generateURL("/greeting"));
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String str = response.readEntity(String.class);
            Assert.assertEquals("Unexpected response content from the server", "\"World\"", str);
        }

        {
            WebTarget target = client.target(generateURL("/numbers"));
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            JsonArray ja = response.readEntity(JsonArray.class);
            Assert.assertEquals("Unexpected response content from the server", 10, ja.size());
        }

        {
            WebTarget target = client.target(generateURL("/resources/numbers"));
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            JsonArray ja = response.readEntity(JsonArray.class);
            Assert.assertEquals("Unexpected response content from the server", 10, ja.size());
        }

    }

}
