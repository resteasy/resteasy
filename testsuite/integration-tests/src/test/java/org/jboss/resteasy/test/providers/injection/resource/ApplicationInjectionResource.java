package org.jboss.resteasy.test.providers.injection.resource;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ResteasyContext;

@Path("/")
public class ApplicationInjectionResource {

   @Context
   ApplicationInjectionApplicationParent application1;

   @Context
   Providers providers;

   @POST
   @Produces("text/plain")
   @Path("test/{param}")
   public Response test(String s, @PathParam("param") String p, @Context ApplicationInjectionApplicationParent application2) {
      if (!application1.getName().equals(application2.getName())) {
         throw new RuntimeException("expecting application1.getName().equals(application2.getName()");
      }
      ContextResolver<String> resolver = providers.getContextResolver(String.class, MediaType.WILDCARD_TYPE);
      return Response.ok(s + "|" + p + "|" + resolver.getContext(null) + "|" + getClass() + ":" + application1.getName()).build();

   }

   @POST
   @Path("exception/{param}")
   public void exception(String s, @PathParam("param") String p, @Context ApplicationInjectionApplicationParent application2) throws ApplicationInjectionException {
      if (!application1.getName().equals(application2.getName())) {
         throw new RuntimeException("expecting application1.getName().equals(application2.getName()");
      }
      ContextResolver<String> resolver = providers.getContextResolver(String.class, MediaType.WILDCARD_TYPE);
      throw new ApplicationInjectionException(s + "|" + p+ "|" + resolver.getContext(null) + "|" + getClass() + ":" + application1.getName());
   }

   @POST
   @Produces("text/plain")
   @Path("async/{param}")
   public CompletionStage<String> text(String s, @PathParam("param") String p, @Context ApplicationInjectionApplicationParent application2) {
      if (!application1.getName().equals(application2.getName())) {
         throw new RuntimeException("expecting application1.getName().equals(application2.getName()");
      }
      CompletableFuture<String> cs = new CompletableFuture<>();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Map<Class<?>, Object> context = ResteasyContext.getContextDataMap();
      executor.submit(
            new Runnable() {
               public void run() {
                  ResteasyContext.pushContextDataMap(context);
                  ContextResolver<String> resolver = providers.getContextResolver(String.class, MediaType.WILDCARD_TYPE);
                  cs.complete(s + "|" + p + "|" + resolver.getContext(null) + "|" + getClass() + ":" + application1.getName());
               }
            });
      return cs;
   }
}
