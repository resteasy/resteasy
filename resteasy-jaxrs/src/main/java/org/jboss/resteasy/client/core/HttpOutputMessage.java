package org.jboss.resteasy.client.core;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import java.io.OutputStream;

/**
 * Abstraction for output HTTP requests
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpOutputMessage implements ClientHttpOutput
{
   protected MultivaluedMap<String, Object> outputHeaders = new MultivaluedMapImpl<String, Object>();
   protected OutputStream outputStream;

   public HttpOutputMessage(OutputStream outputStream)
   {
      this.outputStream = outputStream;
   }

   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return outputHeaders;
   }

   public OutputStream getOutputStream()
   {
      return outputStream;
   }
}
