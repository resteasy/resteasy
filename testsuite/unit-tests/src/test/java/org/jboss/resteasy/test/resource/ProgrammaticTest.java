package org.jboss.resteasy.test.resource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.test.resource.resource.ProgrammaticResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests programmatic creation of resources.
 * @tpSince RESTEasy 3.0.20
 */
public class ProgrammaticTest {

    private static Dispatcher dispatcher;

    @BeforeAll
    public static void BeforeClass() {
        dispatcher = MockDispatcherFactory.createDispatcher();
    }

    @BeforeEach
    public void before() {
        ResteasyContext.getContextDataMap().put(Configurable.class, dispatcher.getProviderFactory());
    }

    /**
     * @tpTestDetails Programmatically create resource class
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testPerRequest() throws Exception {
        Method get = ProgrammaticResource.class.getMethod("get", String.class);
        Method put = ProgrammaticResource.class.getMethod("put", String.class);
        Method setter = ProgrammaticResource.class.getMethod("setHeaders", HttpHeaders.class);
        Field uriInfo = ProgrammaticResource.class.getDeclaredField("uriInfo");
        Field configurable = ProgrammaticResource.class.getDeclaredField("configurable");
        Constructor<?> constructor = ProgrammaticResource.class.getConstructor(Configurable.class);

        ResourceClass resourceclass = new ResourceBuilder().buildRootResource(ProgrammaticResource.class)
                .constructor(constructor).param(0).context().buildConstructor()
                .method(get).get().path("test").produces("text/plain").param(0).queryParam("a").buildMethod()
                .method(put).put().path("test").consumes("text/plain").param(0).messageBody().buildMethod()
                .field(uriInfo).context().buildField()
                .field(configurable).context().buildField()
                .setter(setter).context().buildSetter()
                .buildClass();
        dispatcher.getRegistry().addPerRequestResource(resourceclass);

        MockHttpRequest request = MockHttpRequest.get("/test?a=hello");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        Assertions.assertEquals(response.getContentAsString(), "hello");

        request = MockHttpRequest.put("/test").content("hello".getBytes()).contentType("text/plain");
        response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        Assertions.assertEquals(204, response.getStatus());
        dispatcher.getRegistry().removeRegistrations(resourceclass);

    }

    /**
     * @tpTestDetails Programmatically create singleton
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testSingleton() throws Exception {
        Method get = ProgrammaticResource.class.getMethod("get", String.class);
        Method setter = ProgrammaticResource.class.getMethod("setHeaders", HttpHeaders.class);
        Field uriInfo = ProgrammaticResource.class.getDeclaredField("uriInfo");
        Field configurable = ProgrammaticResource.class.getDeclaredField("configurable");

        ResourceClass resourceclass = new ResourceBuilder().buildRootResource(ProgrammaticResource.class)
                .method(get).get().path("test").produces("text/plain").param(0).queryParam("a").buildMethod()
                .field(uriInfo).context().buildField()
                .field(configurable).context().buildField()
                .setter(setter).context().buildSetter()
                .buildClass();
        ProgrammaticResource resource = new ProgrammaticResource();
        dispatcher.getRegistry().addSingletonResource(resource, resourceclass);

        MockHttpRequest request = MockHttpRequest.get("/test?a=hello");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        Assertions.assertEquals("hello", response.getContentAsString());
        Assertions.assertEquals(1, resource.counter);
        dispatcher.getRegistry().removeRegistrations(resourceclass);
    }

    /**
     * Check that MockHttpResponse handles a user set character set.
     *
     * @throws Exception
     */
    @Test
    public void testCharsetHeader() throws Exception {
        Method get = ProgrammaticResource.class.getMethod("get", String.class);
        Method setter = ProgrammaticResource.class.getMethod("setHeaders", HttpHeaders.class);
        Field uriInfo = ProgrammaticResource.class.getDeclaredField("uriInfo");
        Field configurable = ProgrammaticResource.class.getDeclaredField("configurable");
        Constructor<?> constructor = ProgrammaticResource.class.getConstructor(Configurable.class);

        ResourceClass resourceclass = new ResourceBuilder().buildRootResource(ProgrammaticResource.class)
                .constructor(constructor).param(0).context().buildConstructor()
                .method(get).get().path("test").produces("text/html;charset=UTF-16").param(0).queryParam("a").buildMethod()
                .field(uriInfo).context().buildField()
                .field(configurable).context().buildField()
                .setter(setter).context().buildSetter()
                .buildClass();
        dispatcher.getRegistry().addPerRequestResource(resourceclass);

        MockHttpRequest request = MockHttpRequest.get("/test?a=hello");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
        Assertions.assertEquals(response.getContentAsString(), "hello");

    }

}
