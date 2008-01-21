package org.resteasy.plugins.server.servlet;

import org.resteasy.spi.HttpOutput;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletOutputMessage implements HttpOutput
{
   private HttpServletResponse response;
   private HttpServletResponseHeaders headers;

   public HttpServletOutputMessage(HttpServletResponse response)
   {
      this.response = response;
      headers = new HttpServletResponseHeaders(response);
   }

   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return headers;
   }

   public OutputStream getOutputStream()
   {
      try
      {
         return response.getOutputStream();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
