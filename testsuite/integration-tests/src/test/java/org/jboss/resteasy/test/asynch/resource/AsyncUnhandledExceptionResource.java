package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

@Path("")
public class AsyncUnhandledExceptionResource {

   @Context
   private HttpServletRequest request;

   @POST
   @Path("listener")
   public void a(@Suspended final AsyncResponse async) throws IOException {
      final ServletInputStream inputStream = request.getInputStream();

      inputStream.setReadListener(new ReadListener() {
         @Override
         public void onDataAvailable() throws IOException {
            throw new java.lang.IllegalStateException("a exception");
         }

         @Override
         public void onAllDataRead() throws IOException {
         }

         @Override
         public void onError(Throwable t) {
            async.resume(t);
         }
      });
   }

   @GET
   @Path("thread")
   public void b(@Suspended final AsyncResponse response) {
      new Thread() {
         public void run() {
            try {
               Thread.sleep(1000);
               response.resume(new IllegalStateException("b exception"));
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }
      }.start();
   }
}
