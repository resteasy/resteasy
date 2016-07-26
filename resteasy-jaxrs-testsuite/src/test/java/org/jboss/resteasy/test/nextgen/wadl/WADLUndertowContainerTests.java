package org.jboss.resteasy.test.nextgen.wadl;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.WadlUndertowConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by weli on 6/14/16.
 */
public class WADLUndertowContainerTests {

    private static UndertowJaxrsServer server;

    @Test
    public void test() throws Exception {
        Client client = ClientBuilder.newClient();
        WADLBasicTests basicTest = new WADLBasicTests();
        basicTest.setPort(TestPortProvider.getPort());
        basicTest.setClient(client);
        basicTest.testBasicSet();
        client.close();
    }

    @BeforeClass
    public static void before() throws Exception {
        server = new UndertowJaxrsServer().start();
        WadlUndertowConnector connector = new WadlUndertowConnector();
        connector.deployToServer(server, MyApp.class);
    }

    @AfterClass
    public static void after() throws Exception {
        server.stop();
    }

    @ApplicationPath("/")
    public static class MyApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(BasicResource.class);
            return classes;
        }
    }
}
