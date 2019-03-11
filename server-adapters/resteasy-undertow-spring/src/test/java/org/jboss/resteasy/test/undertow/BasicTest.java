package org.jboss.resteasy.test.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BasicTest {
    private static UndertowJaxrsServer server;

    @Before
    public void before() throws Exception {
        server = new UndertowJaxrsServer().start();
        DeploymentInfo deploymentInfo = Servlets.deployment()
                .setClassLoader(BasicTest.class.getClassLoader())
                .setContextPath("/spring")
                .setDeploymentName("test.war")
                .addServlets(
                        Servlets.servlet("foo", org.springframework.web.servlet.DispatcherServlet.class));

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        manager.deploy();
        PathHandler path = Handlers.path(Handlers.redirect("/myapp"))
                .addPrefixPath("/spring", manager.start());
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(path)
                .build();
        server.start();
    }

    @After
    public void after() {
        server.stop();
    }

    @Test
    public void findConfigurationTest() throws Exception {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("springmvc-resteasy.xml");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] byteArray = buffer.toByteArray();

        new String(byteArray, StandardCharsets.UTF_8);
    }

}
