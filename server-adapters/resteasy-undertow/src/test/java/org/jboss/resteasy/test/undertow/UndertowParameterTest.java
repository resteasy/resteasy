package org.jboss.resteasy.test.undertow;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * RESTEASY-1629
 * Since RESTEasy 3.1.2.Final
 */
public class UndertowParameterTest {

    private static Client client;
    private static UndertowJaxrsServer server;
    private static Map<String, String> contextParams = new HashMap<String, String>();
    private static Map<String, String> initParams = new HashMap<String, String>();

    @ApplicationPath("")
    public static class TestApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(TestResource.class);
            return classes;
        }
    }

    @Path("/")
    public static class TestResource {

        @GET
        @Path("context")
        public Response context(@Context ServletContext context) {
            Enumeration<String> contextEnum = context.getInitParameterNames();
            int count = 0;
            for (; contextEnum.hasMoreElements();) {
                String key = contextEnum.nextElement();
                count++;
            }
            if (count == contextParams.size()) {
                return Response.ok().build();
            } else {
                return Response.status(400).build();
            }
        }

        @GET
        @Path("init")
        public Response init(@Context ServletConfig config) {
            Enumeration<String> initEnum = config.getInitParameterNames();
            int count = 0;
            for (; initEnum.hasMoreElements();) {
                String key = initEnum.nextElement();
                count++;
            }
            if (count == initParams.size()) {
                return Response.ok().build();
            } else {
                return Response.status(400).build();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() throws Exception {
        server = new UndertowJaxrsServer().start();
        ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.setDeploymentSensitiveFactoryEnabled(true);
        deployment.setApplication(new TestApp());
        deployment.start();
        contextParams.put("contextKey1", "contextValue1");
        contextParams.put("contextKey2", "contextValue2");
        initParams.put("initKey1", "initValue1");
        initParams.put("initKey2", "initValue2");
        initParams.put("resteasy.servlet.context.deployment", "false");
        server.setContextParams(contextParams)
                .setInitParams(initParams)
                .setRootResourcePath("/")
                .deploy(deployment);

        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void afterClass() throws Exception {
        server.stop();
        client.close();
    }

    @Test
    public void testContextParameters() throws Exception {
        Response response = client.target("http://localhost:8081/context").request().get(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }

    @Test
    public void testInitParameters() throws Exception {
        Response response = client.target("http://localhost:8081/init").request().get(Response.class);
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }
}
