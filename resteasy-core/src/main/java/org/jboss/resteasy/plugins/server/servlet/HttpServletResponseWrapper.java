package org.jboss.resteasy.plugins.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.ResteasyContext.CloseableContext;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletResponseWrapper implements HttpResponse
{
   public abstract class AsyncOperation
   {
      final CompletableFuture<Void> future = new CompletableFuture<>();
      final OutputStream stream;
      public AsyncOperation(final OutputStream stream)
      {
         this.stream = stream;
      }
      public void work(ServletOutputStream sos) {
         try(CloseableContext c = ResteasyContext.addCloseableContextDataLevel(contextDataMap)){
            doWork(sos);
         }
      }
      protected abstract void doWork(ServletOutputStream sos);
   }

   public class WriteOperation extends AsyncOperation
   {

      private final byte[] bytes;
      private final int offset;
      private final int length;

      public WriteOperation(final OutputStream stream, final byte[] bytes, final int offset, final int length)
      {
         super(stream);
         this.bytes = bytes;
         this.offset = offset;
         this.length = length;
      }

      @Override
      protected void doWork(ServletOutputStream sos)
      {
         try
         {
            stream.write(bytes, offset, length);
            // we only are complete if isReady says we're good to write, otherwise
            // we will be complete in the next onWritePossible or onError
            if(sos == null || sos.isReady()) {
               future.complete(null);
            }
         } catch (IOException e)
         {
            future.completeExceptionally(e);
         }
      }

      @Override
      public String toString()
      {
         return "[write: "+new String(bytes)+"]";
      }
   }

   public class FlushOperation extends AsyncOperation
   {

      public FlushOperation(final OutputStream os)
      {
         super(os);
      }

      @Override
      protected void doWork(ServletOutputStream sos)
      {
         try
         {
            stream.flush();
            // we only are complete if isReady says we're good to write, otherwise
            // we will be complete in the next onWritePossible or onError
            if(sos == null || sos.isReady()) {
               future.complete(null);
            }
         } catch (IOException e)
         {
            future.completeExceptionally(e);
         }
      }

      @Override
      public String toString()
      {
         return "[flush]";
      }
   }

   protected final HttpServletResponse response;
   protected int status = 200;
   protected MultivaluedMap<String, Object> outputHeaders;
   protected final ResteasyProviderFactory factory;
   private OutputStream outputStream;
   protected volatile boolean suppressExceptionDuringChunkedTransfer = true;
   protected final HttpServletRequest request;
   protected final Map<Class<?>, Object> contextDataMap;

   // RESTEASY-1784
   @Override
   public void setSuppressExceptionDuringChunkedTransfer(boolean suppressExceptionDuringChunkedTransfer) {
      this.suppressExceptionDuringChunkedTransfer = suppressExceptionDuringChunkedTransfer;
   }

   @Override
   public boolean suppressExceptionDuringChunkedTransfer() {
      return suppressExceptionDuringChunkedTransfer;
   }

   /**
    * RESTEASY-684 wants to defer access to outputstream until a write happens
    *
    * <p>
    * Note that all locking is on {@code this} and should remain that way to avoid deadlocks on consumers of this
    * stream.
    * </p>
    *
    */
   protected class DeferredOutputStream extends AsyncOutputStream implements WriteListener
   {
      private final ServletOutputStream out;
      // Guarded by this
      private final Queue<AsyncOperation> asyncQueue;
      private final AtomicBoolean asyncRegistered;
      // Guarded by this
      private AsyncOperation lastAsyncOperation;
      private volatile boolean asyncListenerCalled;

      DeferredOutputStream() throws IOException {
         asyncQueue = new LinkedList<>();
         out = response.getOutputStream();
         asyncRegistered = new AtomicBoolean();
      }

      @Override
      public void write(int i) throws IOException
      {
         out.write(i);
      }

      @Override
      public void write(byte[] bytes) throws IOException
      {
         out.write(bytes);
      }

      @Override
      public void write(byte[] bytes, int i, int i1) throws IOException
      {
         out.write(bytes, i, i1);
      }

      @Override
      public void flush() throws IOException
      {
         out.flush();
      }

      @Override
      public void close() throws IOException
      {
         //NOOP (RESTEASY-1650)
      }

      @Override
      public CompletionStage<Void> asyncFlush()
      {
         AsyncOperation op = new FlushOperation(this);
         queue(op);
         return op.future;
      }

      @Override
      public CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length)
      {
         AsyncOperation op = new WriteOperation(this, bytes, offset, length);
         queue(op);
         return op.future;
      }

      private void queue(AsyncOperation op)
      {
         // fetch it from the context directly to avoid having to restore the context just in case we're invoked on a context-less thread
         HttpRequest resteasyRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
         if(request.isAsyncStarted() && !resteasyRequest.getAsyncContext().isOnInitialRequest()) {
            boolean flush = false;
            synchronized(this) {
               if (asyncRegistered.compareAndSet(false, true)) {
                  out.setWriteListener(this);
               }
               if(asyncListenerCalled && out.isReady()) {
                  // it's possible that we startAsync and queue, then queue another event and the stream becomes ready before
                  // onWritePossible is called, which means we need to flush the queue here to guarantee ordering if that happens
                  asyncQueue.add(op);
                  flush = true;
               } else {
                  // just queue
                  asyncQueue.add(op);
               }
            }
            // Invoked outside the lock to avoid deadlocks, the flushQueue itself locks on this
            if (flush) {
               flushQueue();
            }
         } else {
            op.work(null);
         }
      }

      private void flushQueue()
      {
         synchronized (this) {
            if (lastAsyncOperation != null) {
               lastAsyncOperation.future.complete(null);
               lastAsyncOperation = null;
            }

            while (out.isReady() && (lastAsyncOperation = asyncQueue.poll()) != null) {
               lastAsyncOperation.work(out);
            }
         }
      }

      @Override
      public void onWritePossible() {
         asyncListenerCalled = true;
         flushQueue();
      }

      @Override
      public void onError(Throwable t)
      {
         synchronized (this) {
            asyncListenerCalled = true;
            if (lastAsyncOperation != null) {
               lastAsyncOperation.future.completeExceptionally(t);
               lastAsyncOperation = null;
            }
            AsyncOperation op;
            while ((op = asyncQueue.poll()) != null) {
               if (!op.future.isDone())
                  op.future.completeExceptionally(t);
            }
         }
      }
   }

   public HttpServletResponseWrapper(final HttpServletResponse response, final HttpServletRequest request, final ResteasyProviderFactory factory)
   {
      this.response = response;
      this.request = request;
      outputHeaders = new HttpServletResponseHeaders(response, factory);
      this.factory = factory;
      this.contextDataMap = ResteasyContext.getContextDataMap();
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int status)
   {
      this.status = status;
      this.response.setStatus(status);
   }

   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return outputHeaders;
   }

   public synchronized OutputStream getOutputStream() throws IOException
   {
      if (outputStream == null) {
         outputStream = new DeferredOutputStream();
      }
      return outputStream;
   }

   @Override
   public synchronized void setOutputStream(OutputStream os)
   {
      this.outputStream = os;
   }

   public void addNewCookie(NewCookie cookie)
   {
      outputHeaders.add(jakarta.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
   }

   public void sendError(int status) throws IOException
   {
      response.sendError(status);
   }

   public void sendError(int status, String message) throws IOException
   {
      response.sendError(status, message);
   }

   public boolean isCommitted()
   {
      return response.isCommitted();
   }

   public void reset()
   {
      response.reset();
      outputHeaders = new HttpServletResponseHeaders(response, factory);
   }

   @Override
   public void flushBuffer() throws IOException
   {
      response.flushBuffer();
   }
}
