package org.resteasy.spi;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Bridge interface between the base Resteasy JAX-RS implementation and the actual HTTP transport (i.e. a servlet container)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpResponse
{
   int getStatus();

   public void setStatus(int status);

   MultivaluedMap<String, Object> getOutputHeaders();

   OutputStream getOutputStream() throws IOException;

   void addNewCookie(NewCookie cookie);

   void sendError(int status) throws IOException;

   void sendError(int status, String message) throws IOException;

}
