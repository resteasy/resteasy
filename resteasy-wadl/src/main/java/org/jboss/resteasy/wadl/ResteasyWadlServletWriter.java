package org.jboss.resteasy.wadl;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 * @deprecated Using @org.jboss.resteasy.wadl.ResteasyWadlDefaultResource instead.
 */
@Deprecated
public class ResteasyWadlServletWriter extends ResteasyWadlWriter {

   public void writeWadl(String base, HttpServletResponse resp, Map<String, ResteasyWadlServiceRegistry> serviceRegistries)
         throws IOException {
      try {
         ServletOutputStream output = resp.getOutputStream();

         byte[] bytes = getBytes(base, serviceRegistries);
         resp.setContentLength(bytes.length);
         output.write(bytes);
         output.flush();
         output.close();
      } catch (JAXBException e) {
         throw new IOException(e);
      }
   }

   public void writeWadl(String base, HttpServletRequest req, HttpServletResponse resp, Map<String, ResteasyWadlServiceRegistry> serviceRegistries)
         throws IOException {
      writeWadl(base, resp, serviceRegistries);
   }
}
