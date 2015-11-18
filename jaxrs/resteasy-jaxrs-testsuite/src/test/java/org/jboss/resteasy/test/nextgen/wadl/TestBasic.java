package org.jboss.resteasy.test.nextgen.wadl;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.HelloMartianResource;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestBasic {
    private static HttpServer httpServer;
    private static HttpContextBuilder contextBuilder;

    @BeforeClass
    public static void before() throws Exception {
        int port = TestPortProvider.getPort();
        System.out.println("port: " + port);
        httpServer = HttpServer.create(new InetSocketAddress(port), 10);
        contextBuilder = new HttpContextBuilder();
        contextBuilder.getDeployment().getActualResourceClasses().add(HelloMartianResource.class);
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
    public void testBasic() throws InterruptedException {
        // todo add tests
    }

}
