package org.jboss.resteasy.springmvc.test.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.Test;

import javax.servlet.ServletException;

public class BasicTest {
    private static UndertowJaxrsServer server;

    @Test
    public void fooTest() {
        UndertowJaxrsServer server = new UndertowJaxrsServer().start();
        server.start();
        server.stop();
    }

    @Test
    public void basic() throws ServletException {
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
}
