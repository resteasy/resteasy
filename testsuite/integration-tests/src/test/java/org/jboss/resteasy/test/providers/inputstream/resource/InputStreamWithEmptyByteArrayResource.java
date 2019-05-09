package org.jboss.resteasy.test.providers.inputstream.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("")
public class InputStreamWithEmptyByteArrayResource  implements InputStreamWithEmptyByteArrayInterface {

   @Override
   public Response upload(InputStream data) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int b = data.read();
      while (b != -1) {
         baos.write(b);
         b = data.read();
      }
      String s = new String(baos.toByteArray());
      return Response.ok(s).build();
   }
}
