package org.jboss.resteasy.plugins.server.netty.cdi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import java.util.Random;

/**
 * Test for cdi2.0 api SeContainer
 */
@RunWith(Arquillian.class)
public class SeCdiNettyTest {

    private CdiNettyJaxrsServer server;
    private int port;

    @Deployment
    public static JavaArchive createArchive() {
        return ShrinkWrap.create(JavaArchive.class, SeCdiNettyTest.class.getSimpleName() + ".jar")
                .addPackage(CdiRequestDispatcher.class.getPackage())
                .addClasses(EchoResource.class, DefaultExceptionMapper.class);
    }

    @Before
    public void init() {
        while (port < 8000)
            this.port = (int) ((new Random().nextDouble() * 8000) + 1000);
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();

        SeContainer container = initializer.disableDiscovery().addBeanClasses(EchoResource.class)
            .addBeanClasses(DefaultExceptionMapper.class).addExtensions(ResteasyCdiExtension.class).initialize();

        ResteasyCdiExtension cdiExtension = container.select(ResteasyCdiExtension.class).get();
        CdiNettyJaxrsServer netty = new CdiNettyJaxrsServer(container);
        ResteasyDeployment rd = new ResteasyDeployment();
        rd.setActualResourceClasses(cdiExtension.getResources());
        rd.setInjectorFactory(new CdiInjectorFactory(container.getBeanManager()));
        rd.getActualProviderClasses().addAll(cdiExtension.getProviders());
        netty.setDeployment(rd);
        netty.setPort(port);
        netty.setRootResourcePath("/api");
        netty.start();
        this.server = netty;
    }

    @After
    public void shutdown() {
        this.server.stop();
    }

    @Test
    public void testLoadSuccess() {
        String value = ClientBuilder.newClient().target("http://localhost:" + port)
                .path("/api/echo").queryParam("name", "Bob").request().buildGet().invoke(String.class);
        Assert.assertEquals("Hello, Bob!", value);
    }

    @Test
    public void testLoadFailure() {
        Response response = ClientBuilder.newClient().target("http://localhost:" + port)
                .path("/api/echo").queryParam("name", "null").request().buildGet().invoke();
        Assert.assertEquals(406, response.getStatus());
    }
}

