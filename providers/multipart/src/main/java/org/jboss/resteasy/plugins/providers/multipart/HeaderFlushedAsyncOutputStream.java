package org.jboss.resteasy.plugins.providers.multipart;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.spi.AsyncOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderFlushedAsyncOutputStream extends AsyncOutputStream {
   private MultivaluedMap<String, Object> headers;
   private AsyncOutputStream stream;
   private boolean headersFlushed = false;

   public HeaderFlushedAsyncOutputStream(final MultivaluedMap<String, Object> headers,
                                         final AsyncOutputStream delegate) {
      this.headers = headers;
      this.stream = delegate;
   }

   @SuppressWarnings(value = "unchecked")
   protected CompletionStage<Void> flushHeaders() {
      CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
      if (headersFlushed)
         return ret;

      headersFlushed = true;
      RuntimeDelegate delegate = RuntimeDelegate.getInstance();

      for (String key : headers.keySet()) {
         for (Object obj : headers.get(key)) {
            String value;
            RuntimeDelegate.HeaderDelegate headerDelegate = delegate
                  .createHeaderDelegate(obj.getClass());
            if (headerDelegate != null) {
               value = headerDelegate.toString(obj);
            } else {
               value = obj.toString();
            }
            ret = ret.thenCompose(v -> stream.asyncWrite(key.getBytes(StandardCharsets.US_ASCII)))
                    .thenCompose(v -> stream.asyncWrite(AbstractMultipartWriter.COLON_SPACE_BYTES))
                    .thenCompose(v -> stream.asyncWrite(value.getBytes(StandardCharsets.US_ASCII)))
                    .thenCompose(v -> stream.asyncWrite(AbstractMultipartWriter.LINE_SEPARATOR_BYTES));
         }
      }
      return ret.thenCompose(v -> stream.asyncWrite(AbstractMultipartWriter.LINE_SEPARATOR_BYTES));
   }

   @Override
   public void write(int i) throws IOException {
      flushHeaders();
      stream.write(i);
   }

   @Override
   public void write(byte[] bytes) throws IOException {
      flushHeaders();
      stream.write(bytes);
   }

   @Override
   public void write(byte[] bytes, int i, int i1) throws IOException {
      flushHeaders();
      stream.write(bytes, i, i1);
   }

   @Override
   public void flush() throws IOException {
      stream.flush();
   }

   @Override
   public void close() throws IOException {
      stream.close();
   }

   @Override
    public CompletionStage<Void> asyncFlush() {
       return stream.asyncFlush();
    }

    @Override
    public CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length) {
        return flushHeaders()
                .thenCompose(v -> stream.asyncWrite(bytes, offset, length));
    }
}
