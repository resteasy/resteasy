package org.jboss.resteasy.test.nextgen.wadl;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.issues.RESTEASY1246;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlWriter;
import org.jboss.resteasy.wadl.undertow.WadlUndertowConnector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by weli on 6/14/16.
 */
public class WADLUndertowContainerTest {

    private UndertowJaxrsServer server;

    @Test
    public void basicTest() throws Exception {
        WadlUndertowConnector connector = new WadlUndertowConnector();
        connector.deployToServer(server, MyApp.class);
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target("http://127.0.0.1:${port}/base/application.xml".replaceAll("\\$\\{port\\}",
                Integer.valueOf(TestPortProvider.getPort()).toString()));
        Response response = target.request().get();

        // get Application
        org.jboss.resteasy.wadl.jaxb.Application application = response
                .readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
        Assertions.assertNotNull(application, "application not null");
        Assertions.assertEquals(1, application.getResources().size());

        // get BasicResource
        org.jboss.resteasy.wadl.jaxb.Resource basicResource = findResourceByName(application, "/basic");
        Assertions.assertNotNull(basicResource, "basic resouce not null");

        {
            // verify the existence of params
            WADLTestExistenceVerifier paramExistenceVerifier = new WADLTestExistenceVerifier();
            paramExistenceVerifier.createVerifier("name", "name2");
            paramExistenceVerifier.verify(basicResource.getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");

            // verify existence of two methods: "get" and "post"
            WADLTestExistenceVerifier methodExistenceVerifier = new WADLTestExistenceVerifier();
            methodExistenceVerifier.createVerifier("get", "post");
            methodExistenceVerifier.verify(basicResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Method.class,
                    "getId");

            // verify 'post' method has expected id and name
            org.jboss.resteasy.wadl.jaxb.Method post = findMethodById(basicResource, "post");
            Assertions.assertNotNull(post, "post method not null");
            Assertions.assertEquals("POST", post.getName());
            Assertions.assertNotNull(post.getResponse(), "post response not null");
            Assertions.assertNotNull(post.getResponse().get(0).getRepresentation(), "post response representation not null");

            // verify 'get' method
            org.jboss.resteasy.wadl.jaxb.Method get = findMethodById(basicResource, "get");
            Assertions.assertEquals("GET", get.getName());
        }

        {
            // verify existence of resources
            WADLTestExistenceVerifier resourceExistenceVerifier = new WADLTestExistenceVerifier();
            String compositeResourceName = "composite/{pathParam}";

            resourceExistenceVerifier.createVerifier(compositeResourceName);
            resourceExistenceVerifier.verify(basicResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Resource.class,
                    "getPath");

            // verify resource 'intr/{foo}'
            org.jboss.resteasy.wadl.jaxb.Resource compositeResource = findResourceByName(basicResource, compositeResourceName);
            Assertions.assertNotNull(compositeResource);
            Assertions.assertEquals(compositeResourceName, compositeResource.getPath());

            WADLTestExistenceVerifier paramExistenceVerifier = new WADLTestExistenceVerifier();
            paramExistenceVerifier.createVerifier("pathParam", "matrixParam");
            paramExistenceVerifier.verify(compositeResource.getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");

            WADLTestExistenceVerifier methodExistenceVerifier = new WADLTestExistenceVerifier();
            methodExistenceVerifier.createVerifier("composite");
            methodExistenceVerifier.verify(compositeResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Method.class,
                    "getId");

            org.jboss.resteasy.wadl.jaxb.Method compositeMethod = findMethodById(compositeResource, "composite");

            // verify response
            Assertions.assertTrue(compositeMethod.getResponse().get(0).getRepresentation().size() > 0,
                    compositeResourceName + " response contains respresentation");
            Assertions.assertEquals("text/plain",
                    compositeMethod.getResponse().get(0).getRepresentation().get(0).getMediaType());

            WADLTestExistenceVerifier requestVerifier = new WADLTestExistenceVerifier();
            requestVerifier.createVerifier("headerParam", "queryParam", "Cookie");
            requestVerifier.verify(compositeMethod.getRequest().getParam(), org.jboss.resteasy.wadl.jaxb.Param.class,
                    "getName");
        }

        client.close();
    }

    @Test
    public void test1246() {
        WadlUndertowConnector connector = new WadlUndertowConnector();
        connector.deployToServer(server, MyApp1246.class);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(TestPortProvider.generateURL("/base/application.xml"));
        Response response = target.request().get();
        // get Application
        org.jboss.resteasy.wadl.jaxb.Application application = response
                .readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
        org.jboss.resteasy.wadl.jaxb.Method multipleProvides1 = findMethodById(
                findResourceByName(findResourceByName(application, "/issues/1246"), "/provides1"), "multipleProvides1");
        Assertions.assertEquals(2, multipleProvides1.getResponse().get(0).getRepresentation().size(),
                "Multiple representations should be present");
        org.jboss.resteasy.wadl.jaxb.Method multipleProvides2 = findMethodById(
                findResourceByName(findResourceByName(application, "/issues/1246"), "/provides2"), "multipleProvides2");
        Assertions.assertEquals(2, multipleProvides2.getResponse().get(0).getRepresentation().size(),
                "Multiple representations should be present");
        client.close();
    }

    @BeforeEach
    public void before() throws Exception {
        server = new UndertowJaxrsServer().start();
    }

    @AfterEach
    public void after() throws Exception {
        server.stop();
        server = null;
    }

    protected org.jboss.resteasy.wadl.jaxb.Resource findResourceByName(Object target, String resourceName) {
        if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Application.class)) {
            for (org.jboss.resteasy.wadl.jaxb.Resource resource : ((org.jboss.resteasy.wadl.jaxb.Application) target)
                    .getResources().get(0).getResource()) {
                if (resource.getPath().equals(resourceName)) {
                    return resource;
                }
            }
        } else if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class)) {
            for (Object resource : ((org.jboss.resteasy.wadl.jaxb.Resource) target).getMethodOrResource()) {
                if (resource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class)
                        && ((org.jboss.resteasy.wadl.jaxb.Resource) resource).getPath().equals(resourceName)) {
                    return (org.jboss.resteasy.wadl.jaxb.Resource) resource;
                }
            }
        }
        return null;
    }

    protected org.jboss.resteasy.wadl.jaxb.Method findMethodById(org.jboss.resteasy.wadl.jaxb.Resource resource,
            String id) {
        for (Object methodOrResource : resource.getMethodOrResource()) {
            if (methodOrResource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Method.class))
                if (((org.jboss.resteasy.wadl.jaxb.Method) methodOrResource).getId().equals(id))
                    return (org.jboss.resteasy.wadl.jaxb.Method) methodOrResource;
        }
        return null;
    }

    @Path("/")
    private static class MyWadlResource extends ResteasyWadlDefaultResource {
    }

    @ApplicationPath("/base")
    public static class MyApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(BasicResource.class);
            return classes;
        }

        @Override
        public Set<Object> getSingletons() {
            ResteasyWadlDefaultResource defaultResource = new MyWadlResource();
            ResteasyWadlWriter.ResteasyWadlGrammar wadlGrammar = new ResteasyWadlWriter.ResteasyWadlGrammar();
            wadlGrammar.enableSchemaGeneration();
            defaultResource.getWadlWriter().setWadlGrammar(wadlGrammar);

            Set<Object> singletons = new HashSet<>();
            singletons.add(defaultResource);
            return singletons;
        }
    }

    @ApplicationPath("/base")
    public static class MyApp1246 extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(RESTEASY1246.class);
            return classes;
        }

        @Override
        public Set<Object> getSingletons() {
            ResteasyWadlDefaultResource defaultResource = new MyWadlResource();
            ResteasyWadlWriter.ResteasyWadlGrammar wadlGrammar = new ResteasyWadlWriter.ResteasyWadlGrammar();
            wadlGrammar.enableSchemaGeneration();
            defaultResource.getWadlWriter().setWadlGrammar(wadlGrammar);

            Set<Object> singletons = new HashSet<>();
            singletons.add(defaultResource);
            return singletons;
        }

    }
}
