package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceLocatorTest
{
   private static final Logger LOG = Logger.getLogger(ResourceLocatorTest.class);
   private static Dispatcher dispatcher;


   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }


   public static class Subresource2
   {
      @GET
      @Path("stuff/{param}/bar")
      public String doGet(@PathParam("param") String param, @Context UriInfo uri)
      {
         LOG.info("Uri Ancesstors for Subresource2.doGet():");
         Assert.assertEquals(4, uri.getMatchedURIs().size());
         Assert.assertEquals("base/1/resources/subresource2/stuff/2/bar", uri.getMatchedURIs().get(0));
         Assert.assertEquals("base/1/resources/subresource2", uri.getMatchedURIs().get(1));
         Assert.assertEquals("base/1/resources", uri.getMatchedURIs().get(2));
         Assert.assertEquals("", uri.getMatchedURIs().get(3));
         for (String ancestor : uri.getMatchedURIs()) LOG.info("   " + ancestor);


         LOG.info("Uri Ancesstors Object for Subresource2.doGet():");
         Assert.assertEquals(3, uri.getMatchedResources().size());
         Assert.assertEquals(Subresource2.class, uri.getMatchedResources().get(0).getClass());
         Assert.assertEquals(Subresource.class, uri.getMatchedResources().get(1).getClass());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(2).getClass());
         for (Object ancestor : uri.getMatchedResources()) LOG.info("   " + ancestor.getClass().getName());
         Assert.assertEquals("2", param);
         return this.getClass().getName() + "-" + param;
      }
   }


   public static class Subresource
   {
      @GET
      public String doGet(@Context UriInfo uri)
      {
         LOG.info("Uri Ancesstors for Subresource.doGet():");
         List<String> matchedURIs = uri.getMatchedURIs();
         Assert.assertEquals(2, matchedURIs.size());
         Assert.assertEquals("base/1/resources", matchedURIs.get(0));
         Assert.assertEquals("", matchedURIs.get(1));
         for (String ancestor : matchedURIs) LOG.info("   " + ancestor);

         LOG.info("Uri Ancesstors Object for Subresource.doGet():");
         Assert.assertEquals(2, uri.getMatchedResources().size());
         Assert.assertEquals(Subresource.class, uri.getMatchedResources().get(0).getClass());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(1).getClass());
         for (Object ancestor : uri.getMatchedResources()) LOG.info("   " + ancestor.getClass().getName());
         return this.getClass().getName();
      }

      @Path("/subresource2")
      public Object getSubresource2(@Context UriInfo uri)
      {
         LOG.info("Uri Ancesstors for Subresource.getSubresource2():");
         List<String> matchedURIs = uri.getMatchedURIs();
         Assert.assertEquals(3, matchedURIs.size());
         Assert.assertEquals("base/1/resources/subresource2", matchedURIs.get(0));
         Assert.assertEquals("base/1/resources", matchedURIs.get(1));
         Assert.assertEquals("", matchedURIs.get(2));
         for (String ancestor : matchedURIs) LOG.info("   " + ancestor);

         LOG.info("Uri Ancesstors Object for Subresource.getSubresource2():");
         Assert.assertEquals(2, uri.getMatchedResources().size());
         Assert.assertEquals(Subresource.class, uri.getMatchedResources().get(0).getClass());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(1).getClass());
         for (Object ancestor : uri.getMatchedResources()) LOG.info("   " + ancestor.getClass().getName());
         return new Subresource2();
      }
   }

   @Path("/")
   public static class BaseResource
   {
      @Path("base/{param}/resources")
      public Object getSubresource(@PathParam("param") String param, @Context UriInfo uri)
      {
         LOG.info("Here in BaseResource");
         Assert.assertEquals("1", param);
         List<String> matchedURIs = uri.getMatchedURIs();
         Assert.assertEquals(2, matchedURIs.size());
         Assert.assertEquals("base/1/resources", matchedURIs.get(0));
         Assert.assertEquals("", matchedURIs.get(1));
         for (String ancestor : matchedURIs) LOG.info("   " + ancestor);

         LOG.info("Uri Ancesstors Object for Subresource.doGet():");
         Assert.assertEquals(1, uri.getMatchedResources().size());
         Assert.assertEquals(BaseResource.class, uri.getMatchedResources().get(0).getClass());
         return new Subresource();
      }
      @Path("proxy")
      public Subresource3Interface sub3()
      {

         return (Subresource3Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{Subresource3Interface.class}, new InvocationHandler()
         {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
               return method.invoke(new Subresource3(), args);
            }
         });



         //return new Subresource3();
      }
   }

   public interface Subresource3Interface
   {
      @GET
      @Path("3")
      String get(@QueryParam("foo") List<Double> params);
   }


   public static class Subresource3 implements Subresource3Interface
   {
      @Override
      public String get(List<Double> params)
      {
         Assert.assertNotNull(params);
         Assert.assertEquals(2, params.size());
         double p1 = params.get(0);
         double p2 = params.get(1);
         return "Subresource3";
      }
   }

   @Test
   public void testProxiedSubresource() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/proxy/3"));
      ClientResponse res = request.queryParameter("foo", "1.2").queryParameter("foo", "1.3").get();
      Assert.assertEquals(200, res.getStatus());

   }

   @Test
   public void testSubresource() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
      {
         MockHttpRequest request = MockHttpRequest.get("/base/1/resources");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(Subresource.class.getName(), response.getContentAsString());
      }

      /*
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/base/1/resources");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         String response = method.getResponseBodyAsString();
         Assert.assertEquals(Subresource.class.getName(), response);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
      */

      {
         MockHttpRequest request = MockHttpRequest.get("/base/1/resources/subresource2/stuff/2/bar");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(Subresource2.class.getName() + "-2", response.getContentAsString());
      }
   }

   public static class Receiver
   {
      @Path("/head")
      @GET
      public String get()
      {
         return this.getClass().getName();
      }
   }

   public static class QueueReceiver extends Receiver
   {

   }

   @Path("/directory")
   public static class Directory
   {
      @Path("/receivers/{id}")
      public QueueReceiver getReceiver(@PathParam("id") String id)
      {
         return new QueueReceiver();
      }

      @DELETE
      @Path("/receivers/{id}")
      public String closeReceiver(@PathParam("id") String id) throws Exception
      {
         return Directory.class.getName();
      }
   }

   @Test
   public void testSameUri() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      dispatcher.getRegistry().addPerRequestResource(Directory.class);
      {
         MockHttpRequest request = MockHttpRequest.delete("/directory/receivers/1");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(Directory.class.getName(), new String(response.getOutput()));
      }
   }

   @Path("/collection")
   public static class CollectionResource
   {
      @Path("annotation_free_subresource")
      public Object getAnnotationFreeSubResource()
      {
         return new AnnotationFreeSubResource();
      }
   }

   @Produces(MediaType.TEXT_PLAIN)
   public interface RootInterface
   {
      @GET
      String get();

      @Path("{id}")
      Object getSubSubResource(@PathParam("id") String id);
   }

   @Produces(MediaType.TEXT_PLAIN)
   public interface SubInterface extends RootInterface
   {
      @POST
      @Consumes(MediaType.TEXT_PLAIN)
      String post(String s);
   }

   public static abstract class AbstractAnnotationFreeResouce implements RootInterface
   {
      public String get()
      {
         return "got";
      }
   }

   public static class AnnotationFreeSubResource extends AbstractAnnotationFreeResouce implements SubInterface
   {
      public String post(String s)
      {
         return "posted: " + s;
      }

      public Object getSubSubResource(String id)
      {
         return null;
      }
   }

   @Test
   public void testAnnotationFreeSubresource() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      dispatcher.getRegistry().addPerRequestResource(CollectionResource.class);
      {
         MockHttpRequest request = MockHttpRequest.get("/collection/annotation_free_subresource");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("got", response.getContentAsString());
         Assert.assertNotNull(response.getOutputHeaders().get("Content-Type"));
         Assert.assertTrue(response.getOutputHeaders().get("Content-Type").size() > 0);
         Assert.assertEquals(MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8").toString(), response.getOutputHeaders().get("Content-Type").get(0));
      }

      {
         MockHttpRequest request = MockHttpRequest.post("/collection/annotation_free_subresource");
         request.content("hello!".getBytes()).contentType(MediaType.TEXT_PLAIN);
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("posted: hello!", response.getContentAsString());
      }
   }
}
