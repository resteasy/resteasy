package org.jboss.resteasy.test.nextgen.wadl;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.issues.RESTEASY1246;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;
import org.jboss.resteasy.wadl.jaxb.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestAllWADLResources {
    private static HttpServer httpServer;
    private static HttpContextBuilder contextBuilder;
    private static int port = TestPortProvider.getPort() + 1;
    private static Client client = ClientBuilder.newClient();
    String url = "http://127.0.0.1:${port}/application.xml".replaceAll("\\$\\{port\\}",
            Integer.valueOf(port).toString());

    @BeforeClass
    public static void before() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(port), 10);
        contextBuilder = new HttpContextBuilder();
        contextBuilder.getDeployment().getActualResourceClasses().add(BasicResource.class);
        contextBuilder.getDeployment().getActualResourceClasses().add(ResteasyWadlDefaultResource.class);
        contextBuilder.getDeployment().getActualResourceClasses().add(RESTEASY1246.class);
        contextBuilder.bind(httpServer);
        ResteasyWadlDefaultResource.getServices().put("/", ResteasyWadlGenerator.generateServiceRegistry(contextBuilder.getDeployment()));
        httpServer.start();
    }

    @AfterClass
    public static void after() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }

        contextBuilder.cleanup();
        httpServer.stop(0);
        Thread.sleep(100);
    }

    @Test
    public void testBasicResource() throws Exception {
        TestBase basicTest = new TestBase(port, client);
        basicTest.testAll();

    }

    @Test
    public void testResteasy1246() throws Exception {
        WebTarget target = client.target(url);
        Response response = target.request().get();
        // get Application
        org.jboss.resteasy.wadl.jaxb.Application application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
        org.jboss.resteasy.wadl.jaxb.Method multipleProvides = TestBase.findMethodById(TestBase.findResourceByName(application, "/issues/1246"), "multipleProvides");
        assertEquals("Multiple representations should be present", 2, multipleProvides.getResponse().get(0).getRepresentation().size());
    }
}
