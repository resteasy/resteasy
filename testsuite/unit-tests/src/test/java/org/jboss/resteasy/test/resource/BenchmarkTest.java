package org.jboss.resteasy.test.resource;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.Dispatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.UriInfo;

/**
 * @tpSubChapter Profiler helper tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Use to benchmark with a profiler
 * @tpSince RESTEasy 4.1.1
 */
public class BenchmarkTest {

   private static Dispatcher dispatcher;

   @BeforeClass
   public static void BeforeClass() {
      dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addPerRequestResource(HelloResource.class);
   }

   @Before
   public void before() {
      ResteasyContext.getContextDataMap().put(Configurable.class, dispatcher.getProviderFactory());
   }

   @Path("/hello")
   public static class HelloResource {
      @GET
      @Produces("text/plain")
      public String hello() {
         return "hello world";
      }

      @POST
      @Produces("text/plain")
      @Consumes("text/plain")
      public String hello(String name) {
         return "Hello " + name;
      }

   }


   //@Test
   public void testPerRequest() throws Exception
   {
      System.gc();
      //System.out.println("Starting in");
      for (int i = 30; i >= 1; i--) {
          //System.out.println(i);
          Thread.sleep(1000);
      }
      //System.out.println("start");
      testRaw();
      //System.out.println("done");
      Thread.sleep(30000);
   }

   private static final int first = 14442; // 2000000
   private static final int BENCH = 4000000;
   private static final int PROFILE = 2000;
   //@Test
   public void testRaw() {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 5; i++) {
        MockHttpRequest request = MockHttpRequest.create("GET", "/hello", "", "");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);
     }
      long end = System.currentTimeMillis() - start;
      //System.out.println("time took: " + end);
   }

   //@Test
   public void testOne() {
      MockHttpRequest request = MockHttpRequest.create("GET", "/hello", "", "");
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(request, response);
      UriInfo uriInfo = request.getUri();

   }

    //@Test
   public void testUriInfoOptimized() {
      long start = System.currentTimeMillis();
      for (int i = 0; i < BENCH; i++) {
         ResteasyUriInfo uriInfo = new ResteasyUriInfo("http://localhost:8080/hello", "");
         uriInfo.getMatchingPath();
      }
      long end = System.currentTimeMillis() - start;
      //System.out.println("time took: " + end);

   }

   @Test
   public void testContextPath() {
      ResteasyUriInfo uriInfo = new ResteasyUriInfo("http://localhost:8080/hello/world", "/hello");
      Assert.assertEquals("/world", uriInfo.getMatchingPath());
      uriInfo = new ResteasyUriInfo("http://localhost:8080/hello/world", "/hello");
      Assert.assertEquals("/world", uriInfo.getMatchingPath());
      Assert.assertEquals("/world", uriInfo.getPath());

   }

}
