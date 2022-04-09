package org.jboss.resteasy.plugins.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

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
      // Guarded by this
      private final Deque<AsyncOperation> asyncDequeue;
      private final AtomicBoolean asyncRegistered;
      // Guarded by this
      private volatile boolean asyncListenerCalled;
      private volatile ServletOutputStream lazyOut;

      DeferredOutputStream() throws IOException {
         asyncDequeue = new LinkedList<>();
         asyncRegistered = new AtomicBoolean();
      }

      @Override
      public void write(int i) throws IOException
      {
         getServletOutputStream().write(i);
      }

      @Override
      public void write(byte[] bytes) throws IOException
      {
         getServletOutputStream().write(bytes);
      }

      @Override
      public void write(byte[] bytes, int i, int i1) throws IOException
      {
         getServletOutputStream().write(bytes, i, i1);
      }

      @Override
      public void flush() throws IOException
      {
         getServletOutputStream().flush();
      }

      @Override
      public void close() throws IOException
      {
         //NOOP (RESTEASY-1650)
      }

      @Override
      public CompletionStage<Void> asyncFlush()
      {
         AsyncOperation op = new FlushOperation();
         queue(op);
         return op;
      }

      @Override
      public CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length)
      {
         AsyncOperation op = new WriteOperation(bytes, offset, length);
         queue(op);
         return op;
      }

      private void queue(AsyncOperation op)
      {
         // fetch it from the context directly to avoid having to restore the context just in case we're invoked on a context-less thread
         HttpRequest resteasyRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
         if(request.isAsyncStarted() && !resteasyRequest.getAsyncContext().isOnInitialRequest()) {
            boolean flush = false;
            final ServletOutputStream out;
            try {
               out = getServletOutputStream();
            } catch (IOException e) {
               op.completeExceptionally(e);
               return;
            }
            if (asyncRegistered.compareAndSet(false, true)) {
               out.setWriteListener(this);
            }
            synchronized(this) {
               if(asyncListenerCalled && out.isReady()) {
                  // it's possible that we startAsync and queue, then queue another event and the stream becomes ready before
                  // onWritePossible is called, which means we need to flush the queue here to guarantee ordering if that happens
                  asyncDequeue.add(op);
                  flush = true;
               } else {
                  // just queue
                  asyncDequeue.add(op);
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
            final ServletOutputStream out;
            try {
               out = getServletOutputStream();
            } catch (IOException e) {
               onError(e);
               return;
            }
            AsyncOperation currOp;
            while (out.isReady() && (currOp = asyncDequeue.poll()) != null) {
               currOp.work(out);
               if (!currOp.isDone()) {
                  // We add it back so the onWritePossible hook will ultimately lead to a completed future
                  asyncDequeue.add(currOp);
                  out.isReady();
                  break;
               }
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
            AsyncOperation op;
            while ((op = asyncDequeue.poll()) != null) {
               assert !op.isDone();
               op.completeExceptionally(t);
            }
         }
      }

      private ServletOutputStream getServletOutputStream() throws IOException {
         if (lazyOut == null) {
            synchronized (this) {
               if (lazyOut == null) {
                  lazyOut = response.getOutputStream();
               }
            }
         }
         return lazyOut;
      }

      abstract class AsyncOperation extends CompletableFuture<Void>
      {
         boolean workAlreadySubmitted;  // this is confusing, but trying to minimize some change at this point.

         protected abstract void doWork(ServletOutputStream sos) throws IOException;

         public void work(ServletOutputStream sos) {
            if (workAlreadySubmitted) {
               // This is a byproduct of using parent class as a single writelistener and how a deque is leveraged.
               // There is a cleaner design, but it might involve either more objects and/or more significant redesign
               complete(null);
            }
            workAlreadySubmitted = true;
            try(CloseableContext c = ResteasyContext.addCloseableContextDataLevel(contextDataMap)){
               doWork(sos);
               if (sos == null || sos.isReady()) {
                  complete(null);
               }
            } catch (final Exception e) {
               completeExceptionally(e);
            }
         }
      }

      // There is no big need for this OO short of the println..
      class WriteOperation extends AsyncOperation
      {

         private final byte[] bytes;
         private final int offset;
         private final int length;

         WriteOperation(final byte[] bytes, final int offset, final int length)
         {
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
         }

         @Override
         protected void doWork(ServletOutputStream sos) throws IOException
         {
            getServletOutputStream().write(bytes, offset, length);
         }

         @Override
         public String toString()
         {
            return "[write: "+new String(bytes)+"]";
         }
      }

      class FlushOperation extends AsyncOperation
      {

         @Override
         protected void doWork(ServletOutputStream sos) throws IOException
         {
            getServletOutputStream().flush();
         }

         @Override
         public String toString()
         {
            return "[flush]";
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
      outputHeaders.add(javax.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
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
