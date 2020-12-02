package org.jboss.resteasy.test.form.resource;

import org.junit.Assert;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Path("/")
public class FormUrlEncodedResource {
   @Path("/simple")
   @POST
   public StreamingOutput post(@QueryParam("hello") String abs, InputStream entityStream) throws IOException {
      Assert.assertNull(abs);
      final InputStream is = entityStream;
      return new StreamingOutput() {
         public void write(OutputStream output) throws IOException {
            int c;
            while ((c = is.read()) != -1) {
               output.write(c);
            }
         }
      };
   }

   @Path("/form")
   @POST
   @Produces("application/x-www-form-urlencoded")
   @Consumes("application/x-www-form-urlencoded")
   public MultivaluedMap<String, String> post(MultivaluedMap<String, String> form)
   {
      Assert.assertEquals("world", form.getFirst("hello"));
      return form;
   }

   @Path("/form/twoparams")
   @POST
   @Produces("application/x-www-form-urlencoded")
   @Consumes("application/x-www-form-urlencoded")
   public MultivaluedMap<String, String> post2Parameters(MultivaluedMap<String, String> form)
   {
      Assert.assertEquals(2, form.size());
      Assert.assertEquals("world", form.getFirst("hello"));
      Assert.assertEquals("mama", form.getFirst("yo"));
      return form;
   }

   @Path("/RESTEASY-109")
   @POST
   public void post109(MultivaluedMap<String, String> form)
   {
      Assert.assertEquals(form.getFirst("name"), "jon");
      Assert.assertEquals(form.getFirst("address1"), "123 Main St");
      Assert.assertEquals(form.getFirst("address2"), "");
      Assert.assertEquals(form.getFirst("zip"), "12345");
   }

}
