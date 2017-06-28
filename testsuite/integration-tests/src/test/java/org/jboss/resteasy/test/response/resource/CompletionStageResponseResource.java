package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("")
public class CompletionStageResponseResource {
   
   public static final String HELLO = "Hello CompletionStage world!";
   public static final String EXCEPTION = "CompletionStage exception";
   
   @GET
   @Path("text")
   @Produces("text/plain")
   public CompletionStage<String> text() {
      CompletableFuture<String> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(
            new Runnable() {
               public void run() {
                  cs.complete(HELLO);
               }
            });
      return cs;
   }
   
   @GET
   @Path("testclass")
   public CompletionStage<CompletionStageResponseTestClass> entityTestClass() {
      CompletableFuture<CompletionStageResponseTestClass> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(
            new Runnable() {
               public void run() {
                  cs.complete(new CompletionStageResponseTestClass("pdq"));
               }
            });
      return cs;
   }

   @GET
   @Path("response")
   @Produces("text/xxx")
   public CompletionStage<Response> response() {
      CompletableFuture<Response> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(
            new Runnable() {
               public void run() {
                  cs.complete(Response.ok(HELLO, "text/plain").build());
               }
            });
      return cs;
   }
   
   @GET
   @Path("responsetestclass")
   public CompletionStage<Response> responseTestClass() {
      CompletableFuture<Response> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(
            new Runnable() {
               public void run() {
                  cs.complete(Response.ok(new CompletionStageResponseTestClass("pdq")).build());
               }
            });
      return cs;
   }

   @GET
   @Path("null")
   @Produces("text/plain")
   public CompletionStage<String> nullEntity() {
      CompletableFuture<String> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(
            new Runnable() {
               public void run() {
                  cs.complete(null);
               }
            });
      return cs;
   }

   @GET
   @Path("exception/delay")
   @Produces("text/plain")
   public CompletionStage<String> exceptionDelay() {
      CompletableFuture<String> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(
            new Runnable() {
               public void run() {
                  Response response = Response.status(444).entity(EXCEPTION).build();
                  cs.completeExceptionally(new WebApplicationException(response));
               }
            });
      return cs;
   }

   @GET
   @Path("exception/immediate/runtime")
   @Produces("text/plain")
   public CompletionStage<String> exceptionImmediateRuntime() {
      throw new RuntimeException(EXCEPTION + ": expect stacktrace");
   }

   @GET
   @Path("exception/immediate/notruntime")
   @Produces("text/plain")
   public CompletionStage<String> exceptionImmediateNotRuntime() throws Exception {
      throw new Exception( EXCEPTION + ": expect stacktrace");
   }
}
