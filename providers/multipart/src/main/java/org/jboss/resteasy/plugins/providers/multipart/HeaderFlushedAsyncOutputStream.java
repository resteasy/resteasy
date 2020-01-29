package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.spi.AsyncOutputStream;

import java.io.IOException;
import java.util.List;
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
         List<Object> objs = headers.get(key);
         for (Object obj : objs) {
            String value;
            RuntimeDelegate.HeaderDelegate headerDelegate = delegate
                  .createHeaderDelegate(obj.getClass());
            if (headerDelegate != null) {
               value = headerDelegate.toString(obj);
            } else {
               value = obj.toString();
            }
            ret = stream.rxWrite(key.getBytes())
                    .thenCompose(v -> stream.rxWrite(": ".getBytes()))
                    .thenCompose(v -> stream.rxWrite(value.getBytes()))
                    .thenCompose(v -> stream.rxWrite("\r\n".getBytes()));
         }
      }
      return ret.thenCompose(v -> stream.rxWrite("\r\n".getBytes()));
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
    public CompletionStage<Void> rxFlush() {
       return stream.rxFlush();
    }

    @Override
    public CompletionStage<Void> rxWrite(byte[] bytes, int offset, int length) {
        return flushHeaders()
                .thenCompose(v -> stream.rxWrite(bytes, offset, length));
    }
}
