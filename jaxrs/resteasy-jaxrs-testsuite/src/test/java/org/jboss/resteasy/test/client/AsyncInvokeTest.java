package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsyncInvokeTest extends BaseResourceTest
{

   @java.lang.annotation.Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @HttpMethod("PATCH")
   public @interface PATCH
   {
   }

   @Path("/test")
   public static class Resource
   {
      @GET
      @Produces("text/plain")
      public String get() throws Exception
      {
         Thread.sleep(100);
         return "get";
      }

      @PUT
      @Consumes("text/plain")
      public String put(String str) throws Exception
      {
         Thread.sleep(100);
         return "put " + str;
      }

      @POST
      @Consumes("text/plain")
      public String post(String str) throws Exception
      {
         Thread.sleep(100);
         return "post " + str;
      }

      @DELETE
      @Produces("text/plain")
      public String delete() throws Exception
      {
         Thread.sleep(100);
         return "delete";
      }

      @PATCH
      @Produces("text/plain")
      @Consumes("text/plain")
      public String patch(String str) throws Exception
      {
         Thread.sleep(100);
         return "patch " + str;
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Resource.class);
   }

   @Test
   public void testAsync() throws Exception
   {
      ResteasyClient client = buildClient();

      {
         Future<Response> future = client.target(generateURL("/test")).request().async().get();
         Response res = future.get();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("get", entity);

      }

      {
         Future<String> future = client.target(generateURL("/test")).request().async().get(String.class);
         String entity = future.get();
         Assert.assertEquals("get", entity);

      }

      {
         Future<Response> future = client.target(generateURL("/test")).request().async().delete();
         Response res = future.get();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("delete", entity);

      }

      {
         Future<String> future = client.target(generateURL("/test")).request().async().delete(String.class);
         String entity = future.get();
         Assert.assertEquals("delete", entity);

      }
      {
          Future<Response> future = client.target(generateURL("/test")).request().async().put(Entity.text("hello"));
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("put hello", entity);

       }
      {
         Future<String> future = client.target(generateURL("/test")).request().async().put(Entity.text("hello"), String.class);
         String entity = future.get();
         Assert.assertEquals("put hello", entity);

      }

      {
          Future<Response> future = client.target(generateURL("/test")).request().async().post(Entity.text("hello"));
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("post hello", entity);

       }
      {
         Future<String> future = client.target(generateURL("/test")).request().async().post(Entity.text("hello"), String.class);
         String entity = future.get();
         Assert.assertEquals("post hello", entity);

      }

      {
          Future<Response> future = client.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"));
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("patch hello", entity);

       }
      {
         Future<String> future = client.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"), String.class);
         String entity = future.get();
         Assert.assertEquals("patch hello", entity);

      }
      client.close();
   }

   @Test
   public void testAsyncCallback() throws Exception
   {
      ResteasyClient client = buildClient();

      // the callback may be called after the future completes, but it should see always the same result.
      // see ApacheHttpAsyncClient4Engine#submit for a discussion regarding this.

      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<Response> future = client.target(generateURL("/test")).request().async().get(new InvocationCallback<Response>()
         {
            @Override
            public void completed(Response response)
            {
               String entity = response.readEntity(String.class);
               Assert.assertEquals("get", entity);
               latch.countDown();
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         Response res = future.get();
         Assert.assertEquals(200, res.getStatus());
         Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
      }

      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<String> future = client.target(generateURL("/test")).request().async().get(new InvocationCallback<String>()
         {
            @Override
            public void completed(String entity)
            {
               Assert.assertEquals("get", entity);
               latch.countDown();
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         String entity = future.get();
         Assert.assertEquals("get", entity);
         Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
      }

      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<Response> future = client.target(generateURL("/test")).request().async().delete(new InvocationCallback<Response>()
         {
            @Override
            public void completed(Response response)
            {
               String entity = response.readEntity(String.class);
               Assert.assertEquals("delete", entity);
               latch.countDown();
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         Response res = future.get();
         Assert.assertEquals(200, res.getStatus());
         Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
      }

      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<String> future = client.target(generateURL("/test")).request().async().delete(new InvocationCallback<String>()
         {
            @Override
            public void completed(String s)
            {
               Assert.assertEquals("delete", s);
               latch.countDown();
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         String entity = future.get();
         Assert.assertEquals("delete", entity);
         Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
      }
      {
          final CountDownLatch latch = new CountDownLatch(1);
          Future<Response> future = client.target(generateURL("/test")).request().async().put(Entity.text("hello"), new InvocationCallback<Response>()
          {
             @Override
             public void completed(Response response)
             {
                String entity = response.readEntity(String.class);
                Assert.assertEquals("put hello", entity);
                latch.countDown();
             }

             @Override
             public void failed(Throwable error)
             {
             }
          });
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));

       }
       {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<String> future = client.target(generateURL("/test")).request().async().put(Entity.text("hello"), new InvocationCallback<String>()
         {
            @Override
            public void completed(String s)
            {
               Assert.assertEquals("put hello", s);
               latch.countDown();
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         String entity = future.get();
         Assert.assertEquals("put hello", entity);
         Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));

      }

      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<Response> future = client.target(generateURL("/test")).request().async().post(Entity.text("hello"), new InvocationCallback<Response>()
          {
             @Override
             public void completed(Response response)
             {
                String entity = response.readEntity(String.class);
                Assert.assertEquals("post hello", entity);
                latch.countDown();
             }

             @Override
             public void failed(Throwable error)
             {
             }
          });
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));

       }
       {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<String> future = client.target(generateURL("/test")).request().async().post(Entity.text("hello"), new InvocationCallback<String>()
         {
            @Override
            public void completed(String s)
            {
               Assert.assertEquals("post hello", s);
               latch.countDown();
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         String entity = future.get();
         Assert.assertEquals("post hello", entity);
         Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));

      }

      {
          final CountDownLatch latch = new CountDownLatch(1);
          Future<Response> future = client.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"), new InvocationCallback<Response>()
          {
             @Override
             public void completed(Response response)
             {
                String entity = response.readEntity(String.class);
                Assert.assertEquals("patch hello", entity);
                latch.countDown();
             }

             @Override
             public void failed(Throwable error)
             {
             }
          });
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));

       }
      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<String> future = client.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"), new InvocationCallback<String>()
         {
            @Override
            public void completed(String s)
            {
               Assert.assertEquals("patch hello", s);
               latch.countDown();
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         String entity = future.get();
         Assert.assertEquals("patch hello", entity);
         Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));

      }

      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<Response> future = client.target(generateURL("/test")).request().async().get(new InvocationCallback<Response>()
         {
            @Override
            public void completed(Response response)
            {
               String entity = response.readEntity(String.class);
               Assert.assertEquals("get", entity);
               latch.countDown();
               throw new RuntimeException("for the test of it");
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         Assert.assertTrue(latch.await(15, TimeUnit.SECONDS));
         Response res = future.get();
         Assert.assertEquals(200, res.getStatus()); // must not see the runtimeexception of the callback
      }

      {
         final CountDownLatch latch = new CountDownLatch(1);
         Future<String> future = client.target(generateURL("/test")).request().async().get(new InvocationCallback<String>()
         {
            @Override
            public void completed(String s)
            {
               Assert.assertEquals("get", s);
               latch.countDown();
               throw new RuntimeException("for the test of it");
            }

            @Override
            public void failed(Throwable error)
            {
            }
         });
         Assert.assertTrue(latch.await(15, TimeUnit.SECONDS));
         String entity = future.get();
         Assert.assertEquals("get", entity); // must not see the runtimeexception of the callback
      }

      client.close();
   }


   @Test
   public void testSubmit() throws Exception
   {
      ResteasyClient client = buildClient();

      {
         Future<Response> future = client.target(generateURL("/test")).request().buildGet().submit();
         Response res = future.get();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("get", entity);

      }

      {
         Future<String> future = client.target(generateURL("/test")).request().buildGet().submit(String.class);
         String entity = future.get();
         Assert.assertEquals("get", entity);

      }

      {
         Future<Response> future = client.target(generateURL("/test")).request().buildDelete().submit();
         Response res = future.get();
         Assert.assertEquals(200, res.getStatus());
         String entity = res.readEntity(String.class);
         Assert.assertEquals("delete", entity);

      }

      {
         Future<String> future = client.target(generateURL("/test")).request().buildDelete().submit(String.class);
         String entity = future.get();
         Assert.assertEquals("delete", entity);

      }
      {
          Future<Response> future = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).submit();
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("put hello", entity);

       }
      {
         Future<String> future = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).submit(String.class);
         String entity = future.get();
         Assert.assertEquals("put hello", entity);

      }

      {
          Future<Response> future = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).submit();
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("post hello", entity);

       }
      {
         Future<String> future = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).submit(String.class);
         String entity = future.get();
         Assert.assertEquals("post hello", entity);

      }

      {
          Future<Response> future = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).submit();
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus());
          String entity = res.readEntity(String.class);
          Assert.assertEquals("patch hello", entity);

       }
      {
         Future<String> future = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).submit(String.class);
         String entity = future.get();
         Assert.assertEquals("patch hello", entity);

      }
      client.close();
   }

   private ResteasyClient buildClient()
   {
      // only with dependency to apache httpasyncclient and co
      // CloseableHttpAsyncClient asyncClient = HttpAsyncClientBuilder.create().setMaxConnTotal(1).build();
      // return new ResteasyClientBuilder().httpEngine(new ApacheHttpAsyncClient4Engine(asyncClient, true)).build();

      return new ResteasyClientBuilder().build();
   }
}
