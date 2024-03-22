package org.jboss.resteasy.plugins.server.netty.cdi;

import java.util.Random;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.jandex.Index;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.core.scanner.ResourceScanner;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Created by John.Ament on 2/23/14.
 */
@ExtendWith(ArquillianExtension.class)
public class CdiNettyTest {

    private static CdiNettyJaxrsServer server;
    private static int port;

    @Deployment
    public static JavaArchive createArchive() {
        String beans = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n"
                +
                "       version=\"1.1\" bean-discovery-mode=\"all\">\n" +
                "</beans>\n";
        return ShrinkWrap.create(JavaArchive.class, CdiNettyTest.class.getSimpleName() + ".jar")
                .addPackage(CdiRequestDispatcher.class.getPackage())
                .addClasses(EchoResource.class, DefaultExceptionMapper.class)
                .addAsManifestResource(new StringAsset("org.jboss.resteasy.cdi.ResteasyCdiExtension"),
                        "services/jakarta.enterprise.inject.spi.Extension")
                .addAsManifestResource(new StringAsset(beans), "beans.xml");
    }

    @BeforeAll
    public static void init() throws Exception {
        while (port < 8000)
            port = (int) ((new Random().nextDouble() * 8000) + 1000);
        CdiNettyJaxrsServer netty = new CdiNettyJaxrsServer();
        ResteasyDeployment rd = new ResteasyDeploymentImpl();
        final ResourceScanner scanner = ResourceScanner.of(Index.of(EchoResource.class, DefaultExceptionMapper.class));
        rd.getResourceClasses().addAll(scanner.getResources());
        rd.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
        rd.getProviderClasses().addAll(scanner.getProviders());
        netty.setDeployment(rd);
        netty.setPort(port);
        netty.setRootResourcePath("/api");
        netty.start();
        server = netty;
    }

    @AfterAll
    public static void shutdown() {
        server.stop();
    }

    @Test
    public void testLoadSuccess() {
        String value = ClientBuilder.newClient().target("http://localhost:" + port)
                .path("/api/echo").queryParam("name", "Bob").request().buildGet().invoke(String.class);
        Assertions.assertEquals("Hello, Bob!", value);
    }

    @Test
    public void testLoadFailure() {
        Response response = ClientBuilder.newClient().target("http://localhost:" + port)
                .path("/api/echo").queryParam("name", "null").request().buildGet().invoke();
        Assertions.assertEquals(406, response.getStatus());
    }
}
