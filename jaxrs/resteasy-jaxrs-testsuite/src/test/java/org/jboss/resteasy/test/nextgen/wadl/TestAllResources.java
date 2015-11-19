package org.jboss.resteasy.test.nextgen.wadl;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;
import org.jboss.resteasy.wadl.jaxb.Application;
import org.jboss.resteasy.wadl.jaxb.Param;
import org.jboss.resteasy.wadl.jaxb.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestAllResources {
    private static HttpServer httpServer;
    private static HttpContextBuilder contextBuilder;
    private static int port = TestPortProvider.getPort();

    @BeforeClass
    public static void before() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(port), 10);
        contextBuilder = new HttpContextBuilder();
        contextBuilder.getDeployment().getActualResourceClasses().add(BasicResource.class);
        contextBuilder.getDeployment().getActualResourceClasses().add(ResteasyWadlDefaultResource.class);
        contextBuilder.bind(httpServer);
        ResteasyWadlDefaultResource.getServices().put("/", ResteasyWadlGenerator.generateServiceRegistry(contextBuilder.getDeployment()));
        httpServer.start();
    }

    @AfterClass
    public static void after() throws Exception {
        contextBuilder.cleanup();
        httpServer.stop(0);
    }

    @Test
    public void testBasicResource() throws InterruptedException {
        Client client = ClientBuilder.newClient();
        String url = "http://127.0.0.1:${port}/application.xml".replaceAll("\\$\\{port\\}",
                Integer.valueOf(port).toString());
        WebTarget target = client.target(url);
        Response response = target.request().get();

        // get Application
        org.jboss.resteasy.wadl.jaxb.Application application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
        assertNotNull(application);
        assertEquals(1, application.getResources().size());

        // get BasicResource
        String resourceName = "/basic";
        org.jboss.resteasy.wadl.jaxb.Resource basicResource = findResourceByName(application, resourceName);
        assertNotNull(basicResource);

        // verify params
        Map<String, Boolean> verifier = new HashMap<>();
        verifier.put("name", false);
        verifier.put("name2", false);

        for (Param param : basicResource.getParam()) {
            if ("name".equals(param.getName()))
                verifier.put("name", true);
            else if ("name2".equals(param.getName()))
                verifier.put("name2", true);
        }

        assertTrue(allTrue(verifier));

//        while (true);
    }

    private boolean allTrue(Map<String, Boolean> verifier) {
        boolean flag = true;
        for (Boolean value: verifier.values()) {
            if (value.booleanValue() == false) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private Resource findResourceByName(Application application, String resourceName) {
        for (Resource resource : application.getResources().get(0).getResource()) {
            if (resource.getPath().equals(resourceName)) {
                return resource;
            }
        }
        return null;
    }

}
