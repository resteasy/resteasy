package org.jboss.resteasy.test.client.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
public class TestResource
{
   @HttpMethod("TRACE")
   @Target(value= ElementType.METHOD)
   @Retention(value= RetentionPolicy.RUNTIME)
   public @interface TRACE {
   }

   @HttpMethod("METHOD")
   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface METHOD {
   }

   @Path("get")
   @GET
   public Response get()
   {
      return Response.ok("get").build();
   }

   @GET
   @Path("sleep")
   @Produces("text/plain")
   public CompletionStage<String> sleep() {
      CompletableFuture<String> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(() -> {
         try {
            Thread.sleep(3000L);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
         cs.complete("get");
      });
      return cs;
   }

   @Path("put")
   @PUT
   @Consumes(MediaType.TEXT_PLAIN)
   public Response put(String s)
   {
      return Response.ok(s).build();
   }

   @Path("post")
   @POST
   @Consumes(MediaType.TEXT_PLAIN)
   public Response post(String s)
   {
      return Response.ok(s).build();
   }

   @Path("delete")
   @DELETE
   public Response delete()
   {
      return Response.ok("delete").build();
   }

   @Path("head")
   @HEAD
   public Response head()
   {
      return Response.noContent().header("key", "head").build();
   }

   @Path("options")
   @OPTIONS
   public Response options()
   {
      return Response.ok("options").build();
   }

   @Path("trace")
   @TRACE
   public Response trace()
   {
      return Response.ok("trace").build();
   }

   @Path("method")
   @METHOD
   public Response method()
   {
      return Response.ok("method").build();
   }

   @Path("methodEntity")
   @METHOD
   public Response methodEntity(String s)
   {
      return Response.ok(s).build();
   }
}
