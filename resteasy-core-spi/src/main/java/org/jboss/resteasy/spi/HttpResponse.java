package org.jboss.resteasy.spi;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;

/**
 * Bridge interface between the base Resteasy JAX-RS implementation and the actual HTTP transport (i.e. a servlet container)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpResponse extends Closeable
{
   int getStatus();

   void setStatus(int status);

   MultivaluedMap<String, Object> getOutputHeaders();

   OutputStream getOutputStream() throws IOException;
   void setOutputStream(OutputStream os);

   default AsyncOutputStream getAsyncOutputStream() throws IOException {
       OutputStream os = getOutputStream();
       return os instanceof AsyncOutputStream ? (AsyncOutputStream)os : new BlockingAsyncOutputStream(os);
   }

   void addNewCookie(NewCookie cookie);

   void sendError(int status) throws IOException;

   void sendError(int status, String message) throws IOException;

   boolean isCommitted();

   /**
    * reset status and headers.  Will fail if response is committed
    */
   void reset();

   default void close() throws IOException {
      // RESTEASY-1650
      getOutputStream().close();
   }

   void flushBuffer() throws IOException;

   // RESTEASY-1784
   default void setSuppressExceptionDuringChunkedTransfer(boolean suppressExceptionDuringChunkedTransfer) {};

   default boolean suppressExceptionDuringChunkedTransfer() {
      return true;
   }
}
