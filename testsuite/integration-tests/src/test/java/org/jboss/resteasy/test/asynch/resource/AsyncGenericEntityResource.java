package org.jboss.resteasy.test.asynch.resource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.GenericEntity;

@Path("")
public class AsyncGenericEntityResource {

   @GET
   @Produces("application/xml")
   @Path("test")
   public void get(@Suspended final AsyncResponse response) {
      response.setTimeout(10000, TimeUnit.MILLISECONDS);
      Thread t = new Thread() {

         @Override
         public void run() {
            try {
               Thread.sleep(100);
               List<String> list = new ArrayList<String>();
               list.add("abc");
               GenericEntity<List<String>> entity = new GenericEntity<List<String>>(list) {};
               response.resume(entity);
            } catch (Exception e) {
               StringWriter errors = new StringWriter();
               e.printStackTrace(new PrintWriter(errors));
            }
         }
      };
      t.start();
   }
}