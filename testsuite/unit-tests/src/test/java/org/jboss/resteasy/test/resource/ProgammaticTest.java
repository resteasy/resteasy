package org.jboss.resteasy.test.resource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.test.resource.resource.ProgrammaticResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests programmatic creation of resources.
 * @tpSince RESTEasy 3.0.20
 */
public class ProgammaticTest {
   
   private static Dispatcher dispatcher;
   
   @BeforeClass
   public static void BeforeClass() {
      dispatcher = MockDispatcherFactory.createDispatcher();
   }
   
   @Before
   public void before() {
      ResteasyProviderFactory.getContextDataMap().put(Configurable.class, dispatcher.getProviderFactory());
   }
   
   /**
    * @tpTestDetails Programmatically create resource class
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testPerRequest() throws Exception
   {
      Method get = ProgrammaticResource.class.getMethod("get", String.class);
      Method put = ProgrammaticResource.class.getMethod("put", String.class);
      Method setter = ProgrammaticResource.class.getMethod("setHeaders", HttpHeaders.class);
      Field uriInfo = ProgrammaticResource.class.getDeclaredField("uriInfo");
      Field configurable = ProgrammaticResource.class.getDeclaredField("configurable");
      Constructor<?> constructor = ProgrammaticResource.class.getConstructor(Configurable.class);

      ResourceClass resourceclass = ResourceBuilder.rootResource(ProgrammaticResource.class)
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
      Assert.assertEquals(response.getContentAsString(), "hello");
      
      request = MockHttpRequest.put("/test").content("hello".getBytes()).contentType("text/plain");
      response = new MockHttpResponse();
      dispatcher.invoke(request, response);
      Assert.assertEquals(204, response.getStatus());
      dispatcher.getRegistry().removeRegistrations(resourceclass);

   }

   /**
    * @tpTestDetails Programmatically create singleton
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testSingleton() throws Exception
   {
      Method get = ProgrammaticResource.class.getMethod("get", String.class);
      Method setter = ProgrammaticResource.class.getMethod("setHeaders", HttpHeaders.class);
      Field uriInfo = ProgrammaticResource.class.getDeclaredField("uriInfo");
      Field configurable = ProgrammaticResource.class.getDeclaredField("configurable");

      ResourceClass resourceclass = ResourceBuilder.rootResource(ProgrammaticResource.class)
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
      Assert.assertEquals("hello", response.getContentAsString());
      Assert.assertEquals(1, resource.counter);
      dispatcher.getRegistry().removeRegistrations(resourceclass);
   }

   /**
    * Check that MockHttpResponse handles a user set character set.
    * @throws Exception
    */
   @Test
   public void testCharsetHeader() throws Exception
   {
      Method get = ProgrammaticResource.class.getMethod("get", String.class);
      Method setter = ProgrammaticResource.class.getMethod("setHeaders", HttpHeaders.class);
      Field uriInfo = ProgrammaticResource.class.getDeclaredField("uriInfo");
      Field configurable = ProgrammaticResource.class.getDeclaredField("configurable");
      Constructor<?> constructor = ProgrammaticResource.class.getConstructor(Configurable.class);

      ResourceClass resourceclass = ResourceBuilder.rootResource(ProgrammaticResource.class)
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
      Assert.assertEquals(response.getContentAsString(), "hello");

   }

}
