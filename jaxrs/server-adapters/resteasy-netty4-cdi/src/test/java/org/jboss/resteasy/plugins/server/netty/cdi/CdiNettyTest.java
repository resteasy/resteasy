package org.jboss.resteasy.plugins.server.netty.cdi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.Random;

/**
 * Created by John.Ament on 2/23/14.
 */
@RunWith(Arquillian.class)
public class CdiNettyTest {

    private CdiNettyJaxrsServer server;
    private int port;

    @Deployment
    public static JavaArchive createArchive() {
        String beans = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n" +
                "       version=\"1.1\" bean-discovery-mode=\"all\">\n" +
                "</beans>\n";
        return ShrinkWrap.create(JavaArchive.class, CdiNettyTest.class.getSimpleName() + ".jar")
                .addPackage(CdiRequestDispatcher.class.getPackage())
                .addClasses(EchoResource.class, DefaultExceptionMapper.class)
                .addAsManifestResource(new StringAsset("org.jboss.resteasy.cdi.ResteasyCdiExtension"),
                        "services/javax.enterprise.inject.spi.Extension")
                .addAsManifestResource(new StringAsset(beans), "beans.xml");
    }

    @Before
    public void init() {
        while (port < 8000)
            this.port = (int) ((new Random().nextDouble() * 8000) + 1000);
        ResteasyCdiExtension cdiExtension = CDI.current().select(ResteasyCdiExtension.class).get();
        CdiNettyJaxrsServer netty = new CdiNettyJaxrsServer();
        ResteasyDeployment rd = new ResteasyDeployment();
        rd.setActualResourceClasses(cdiExtension.getResources());
        rd.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
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
