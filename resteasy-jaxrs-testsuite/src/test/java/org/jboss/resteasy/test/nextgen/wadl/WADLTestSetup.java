package org.jboss.resteasy.test.nextgen.wadl;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.issues.RESTEASY1246;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.InetSocketAddress;

/**
 * Created by weli on 6/14/16.
 */
public abstract class WADLTestSetup {

    private static HttpServer httpServer;
    protected static HttpContextBuilder contextBuilder;
    protected static int port = TestPortProvider.getPort() + 1;
    protected static Client client = ClientBuilder.newClient();

    public static void setPort(int port) {
        WADLTestSetup.port = port;
    }

    public static void setClient(Client client) {
        WADLTestSetup.client = client;
    }

    public static String getUrl() {
        return "http://127.0.0.1:${port}/application.xml".replaceAll("\\$\\{port\\}",
                Integer.valueOf(port).toString());
    }

    @BeforeClass
    public static void before() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(port), 10);
        contextBuilder = new HttpContextBuilder();
        contextBuilder.getDeployment().getActualResourceClasses().add(BasicResource.class);
        contextBuilder.getDeployment().getActualResourceClasses().add(ResteasyWadlDefaultResource.class);
        contextBuilder.getDeployment().getActualResourceClasses().add(RESTEASY1246.class);
        contextBuilder.bind(httpServer);
        ResteasyWadlDefaultResource.getServices().put("/", ResteasyWadlGenerator.generateServiceRegistry(contextBuilder.getDeployment()));
        httpServer.start();
    }

    @AfterClass
    public static void after() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        contextBuilder.cleanup();
        httpServer.stop(0);
        Thread.sleep(100);
    }

    protected org.jboss.resteasy.wadl.jaxb.Method findMethodById(org.jboss.resteasy.wadl.jaxb.Resource resource, String id) {
        for (Object methodOrResource : resource.getMethodOrResource()) {
            if (methodOrResource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Method.class))
                if (((org.jboss.resteasy.wadl.jaxb.Method) methodOrResource).getId().equals(id))
                    return (org.jboss.resteasy.wadl.jaxb.Method) methodOrResource;
        }
        return null;
    }

    protected org.jboss.resteasy.wadl.jaxb.Resource findResourceByName(Object target, String resourceName) {
        if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Application.class)) {
            for (org.jboss.resteasy.wadl.jaxb.Resource resource : ((org.jboss.resteasy.wadl.jaxb.Application) target).getResources().get(0).getResource()) {
                if (resource.getPath().equals(resourceName)) {
                    return resource;
                }
            }
        } else if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class)) {
            for (Object resource : ((org.jboss.resteasy.wadl.jaxb.Resource) target).getMethodOrResource()) {
                if (resource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class) && ((org.jboss.resteasy.wadl.jaxb.Resource) resource).getPath().equals(resourceName)) {
                    return (org.jboss.resteasy.wadl.jaxb.Resource) resource;
                }
            }
        }
        return null;
    }
}
