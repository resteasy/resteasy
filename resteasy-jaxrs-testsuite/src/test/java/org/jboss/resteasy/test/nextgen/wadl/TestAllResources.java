package org.jboss.resteasy.test.nextgen.wadl;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestAllResources {
    private static HttpServer httpServer;
    private static HttpContextBuilder contextBuilder;
    private static int port = TestPortProvider.getPort() + 1;
    private static Client client = ClientBuilder.newClient();

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
        BasicTest basicTest = new BasicTest(port, client);
        basicTest.testBasicResource();
    }
}
