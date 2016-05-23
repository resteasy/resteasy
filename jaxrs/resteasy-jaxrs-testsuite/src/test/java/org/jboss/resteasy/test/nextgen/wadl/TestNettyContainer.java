package org.jboss.resteasy.test.nextgen.wadl;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.NettyContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestNettyContainer {
    private static NettyJaxrsServer netty;
    private static int port = TestPortProvider.getPort() + 2;
    private static Client client = ClientBuilder.newClient();

    @BeforeClass
    public static void setup() throws Exception {

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setSecurityEnabled(true);

        netty = new NettyJaxrsServer();
        netty.setDeployment(deployment);
        netty.setPort(port);
        netty.setRootResourcePath("");
        netty.setSecurityDomain(null);
        netty.start();

        deployment.getRegistry().addPerRequestResource(BasicResource.class);
        deployment.getRegistry().addPerRequestResource(ResteasyWadlDefaultResource.class);
        ResteasyWadlDefaultResource.getServices().put("/", ResteasyWadlGenerator.generateServiceRegistry(deployment));
    }

    @AfterClass
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        NettyContainer.stop();
        Thread.sleep(100);
    }

    @Test
    public void test() throws Exception {
        BasicTest basicTest = new BasicTest(port, client);
        basicTest.testBasicResource();
    }
}
