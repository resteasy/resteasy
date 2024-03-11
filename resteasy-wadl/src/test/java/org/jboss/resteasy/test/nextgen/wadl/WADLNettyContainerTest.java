package org.jboss.resteasy.test.nextgen.wadl;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.issues.RESTEASY1246;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class WADLNettyContainerTest {
    private static NettyJaxrsServer netty;
    private static int port = TestPortProvider.getPort();
    private static Client client = ClientBuilder.newClient();

    @BeforeAll
    public static void setup() throws Exception {
        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.setSecurityEnabled(true);

        netty = new NettyJaxrsServer();
        netty.setDeployment(deployment);
        netty.setPort(port);
        netty.setRootResourcePath("");
        netty.setSecurityDomain(null);
        netty.start();

        deployment.getRegistry().addPerRequestResource(BasicResource.class);
        deployment.getRegistry().addPerRequestResource(RESTEASY1246.class);

        ResteasyWadlDefaultResource defaultResource = new MyWadlResource();

        deployment.getRegistry().addSingletonResource(defaultResource);

        defaultResource.getServices().put("/", ResteasyWadlGenerator.generateServiceRegistry(deployment));
    }

    @AfterAll
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        netty.stop();
        netty = null;
    }

    @Test
    public void test() throws Exception {
        TestWadlFunctions basicTest = new TestWadlFunctions();
        basicTest.setClient(client);
        basicTest.testBasicSet();
        basicTest.testResteasy1246();
    }
}
