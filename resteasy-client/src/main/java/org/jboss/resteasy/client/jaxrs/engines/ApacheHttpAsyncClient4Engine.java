package org.jboss.resteasy.client.jaxrs.engines;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.ContentTooLongException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.entity.ContentInputStream;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SharedInputBuffer;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * AsyncClientHttpEngine using apache http components HttpAsyncClient 4.<p>
 *
 * Some words of caution: <ul>
 * <li>Asynchronous IO means non-blocking IO utilizing few threads, typically at most as much threads as number of cores.
 * As such, performance may profit from fewer thread switches and less memory usage due to fewer thread-stacks. But doing
 * synchronous, blocking IO (the invoke-methods not returning a future) may suffer, because the data has to be transferred
 * piecewiese to/from the io-threads.</li>
 * <li>Request-Entities are fully buffered in memory, thus this engine is unsuitable for very large uploads.</li>
 * <li>Response-Entities are buffered in memory, except if requesting a Response, InputStream or Reader as Result. Thus
 * for large downloads or COMET one of these three return types must be requested, but there may be a performance penalty
 * because the response-body is transferred piecewise from the io-threads. When using InvocationCallbacks, the response is
 * always fully buffered in memory.</li>
 * <li>InvocationCallbacks are called from within the io-threads and thus must not block or else the application may
 * slow down to a halt. Reading the response is safe (because the response is buffered in memory), as are other async
 * (and in-memory) Client-invocations (the submit-calls returning a future not containing Response, InputStream or Reader).
 * Again, there must be no blocking IO inside InvocationCallback! (If you are wondering why not to allow blocking calls by
 * wrapping InvocationCallbacks in extra threads: Because then the main advantage of async IO, less threading, is lost.)
 * <li>InvocationCallbacks may be called seemingly "after" the future-object returns. Thus, responses should be handled
 * solely in the InvocationCallback.</li>
 * <li>InvocationCallbacks will see the same result as the future-object and vice versa. Thus, if the invocationcallback
 * throws an exception, the future-object will not see it. Another reason to handle responses only in the InvocationCallback.
 * </li>
 * </ul>
 * @author Markus Kull
 */
public class ApacheHttpAsyncClient4Engine implements AsyncClientHttpEngine, Closeable
{
   protected final CloseableHttpAsyncClient client;
   protected final boolean closeHttpClient;

   public ApacheHttpAsyncClient4Engine(CloseableHttpAsyncClient client, boolean closeHttpClient)
   {
      if (client == null) throw new NullPointerException("client");
      this.client = client;
      this.closeHttpClient = closeHttpClient;
      if (closeHttpClient && !client.isRunning()) {
         client.start();
      }
   }

   @Override
   public void close()
   {
      if (closeHttpClient)
      {
         IOUtils.closeQuietly(client);
      }
   }

   @Override
   public SSLContext getSslContext()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public HostnameVerifier getHostnameVerifier()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public ClientResponse invoke(ClientInvocation request)
   {
      // Doing blocking requests with an async httpclient is quite useless.
      // But it is better to use the same httpclient in any case just for sharing+configuring only one connectionpool.
      Future<ClientResponse> future = submit(request, false, null, new ResultExtractor<ClientResponse>() {
         @Override
         public ClientResponse extractResult(ClientResponse response)
         {
            return response;
         }
      });
      try
      {
         return future.get();
      }
      catch (InterruptedException e)
      {
         future.cancel(true);
         throw clientException(e, null);
      }
      catch (ExecutionException e)
      {
         throw clientException(e.getCause(), null);
      }
   }

   @Override
   public <T> Future<T> submit(
      ClientInvocation request, boolean buffered, InvocationCallback<T> callback, ResultExtractor<T> extractor)
   {
      HttpUriRequest httpRequest = buildHttpRequest(request);

      if (buffered)
      {
         // Request+Response fully buffered in memory. Optional callback is called inside io-thread after response-body and
         // after the returned future is signaled to be completed.
         //
         // This differs to Resteasy 3.0.8 and earlier (which called the callback before the future completed) due to the
         // following reasons:
         //   * ApacheHttpcomponents BasicFuture, guavas ListenableFuture and also jersey calls the callback after completing
         //     the future. The earlier Resteasy-behaviour may be more "safe" but any users switching from resteasy to another
         //     jax-rs implementation may encounter a nasty surprise.
         //   * ensure the result returned by the future is the same given to the callback.
         //   * As good practice, the result should only be handled in one place (future OR callback, not both)
         //   * Invocation-javadoc says "the underlying response instance will be automatically closed" seemingly implying
         //     the future-response is unusable (bc. closed) together with a callback
         // Of course the one big drawback is that exceptions inside the callback are not visible to the application,
         // but callbacks are mostly treated as fire-and-forget, meaning their result is not checked anyway.
         HttpAsyncRequestProducer requestProducer = HttpAsyncMethods.create(httpRequest);
         HttpAsyncResponseConsumer<T> responseConsumer = new BufferingResponseConsumer<T>(request, extractor);
         FutureCallback<T> httpCallback = callback != null ? new CallbackAdapter<T>(callback) : null;

         return client.execute(requestProducer, responseConsumer, httpCallback);
      }
      else
      {
         // unbuffered: Future returns immediately after headers. Reading the response-stream blocks, but one may check
         // InputStream#available() to prevent blocking.

         // would be easy to call an InvocationCallback after response-BODY, but cant see any usecase for it.
         if (callback != null) throw new IllegalArgumentException("unbuffered InvocationCallback is not supported");

         HttpAsyncRequestProducer requestProducer = HttpAsyncMethods.create(httpRequest);
         StreamingResponseConsumer<T> responseConsumer = new StreamingResponseConsumer<T>(request, extractor);

         Future<T> httpFuture = client.execute(requestProducer, responseConsumer, null);
         return responseConsumer.future(httpFuture);
      }
   }


   /**
    * ResponseConsumer which transfers the response piecewise from the io-thread to the blocking handler-thread.
    * {@link #future(Future)} returns a Future which completes immediately after receiving the response-headers
    * but reading the response-inputstream blocks until data is available.
    */
   private static class StreamingResponseConsumer<T> implements HttpAsyncResponseConsumer<T>
   {
      private static final IOException unallowedBlockingReadException = new IOException("blocking reads inside an async io-handler are not allowed") {
         public synchronized Throwable fillInStackTrace() {
            //do nothing and return
            return this;
        }
      };
      
      private ClientConfiguration configuration;
      private Map<String, Object> properties;
      private ResultExtractor<T> extractor;

      private ResultFuture<T> future;
      private SharedInputStream sharedStream;

      private volatile boolean hasResult;
      private volatile T result;
      private volatile Exception exception;
      private volatile boolean completed;

      public StreamingResponseConsumer(ClientInvocation request, ResultExtractor<T> extractor)
      {
         this.configuration = request.getClientConfiguration();
         this.properties = request.getMutableProperties();
         this.extractor = extractor;
      }

      private void releaseResources()
      {
         this.configuration = null;
         this.properties = null;
         this.extractor = null;

         this.future = null;
         this.sharedStream = null;
      }

      public synchronized Future<T> future(Future<T> httpFuture)
      {
         if (completed)
         {  // already failed or fully buffered
            return httpFuture;
         }
         future = new ResultFuture<T>(httpFuture);
         future.copyHttpFutureResult();
         if (!future.isDone() && hasResult)
         { // response(-headers) is available, but not yet the full response-stream. Return immediately the result
            future.completed(getResult());
         }
         return future;
      }

      @Override
      public synchronized void responseReceived(HttpResponse httpResponse) throws IOException, HttpException
      {
         SharedInputStream sharedStream = null;
         ConnectionResponse clientResponse = null;
         T result = null;
         Exception exception = null;

         boolean success = false;
         try {
            clientResponse = new ConnectionResponse(configuration, properties);
            copyResponse(httpResponse, clientResponse);
            final HttpEntity entity = httpResponse.getEntity();
            if (entity != null)
            {
               sharedStream = new SharedInputStream(new SharedInputBuffer(16 * 1024));
               // one could also set the stream after extracting the response, but this would prevent wrapping the stream
               clientResponse.setConnection(sharedStream);
               sharedStream.setException(unallowedBlockingReadException);
               result = extractor.extractResult(clientResponse);
               sharedStream.setException(null);
            }
            else
            {
               result = extractor.extractResult(clientResponse);
            }
            success = true;
         }
         catch(Exception e)
         {
            exception = clientException(e, clientResponse);
         }
         finally
         {
            if (success)
            {
               this.sharedStream = sharedStream;
               this.result = result;
               this.hasResult = true;
               if (future != null) future.completed(result);
            }
            else
            {
               this.exception = exception;
               completed = true;
               if (future != null) future.failed(exception);
               releaseResources();
            }
         }
      }

      @Override
      public synchronized void consumeContent(ContentDecoder decoder, IOControl ioctrl) throws IOException
      {
         if (sharedStream != null) sharedStream.consumeContent(decoder, ioctrl);
      }

      @Override
      public synchronized void responseCompleted(HttpContext context)
      {
         this.completed = true;
         try
         {
            if (sharedStream != null)
            {  // only needed in case of empty response body (=null ioctrl)
               sharedStream.consumeContent(EndOfStream.INSTANCE, null);
            }
         }
         catch (IOException ioe)
         { // cannot happen
            throw new RuntimeException(ioe);
         }
         finally
         {
            releaseResources();
         }
      }

      @Override
      public Exception getException()
      {
         return exception;
      }

      @Override
      public T getResult()
      {
         return result;
      }

      @Override
      public boolean isDone()
      {  // cancels in case of closing the SharedInputStream
         return completed;
      }

      @Override
      public synchronized void close()
      {
         completed = true;
         ResultFuture<T> future = this.future;
         if (future != null)
         {
            // if connect fails, then the httpclient just calls close() after setting its future, but never our failed().
            // so copy the httpFuture-result into our ResultFuture.
            future.copyHttpFutureResult();
            if (!future.isDone())
            { // doesnt happen?
               future.failed(clientException(new IOException("connect failed"), null));
            }
         }
         releaseResources();
      }

      @Override
      public synchronized void failed(Exception ex)
      {
         completed = true;
         if (future != null) future.failed(clientException(ex, null));
         if (sharedStream != null)
         {
            sharedStream.setException(ioException(ex));
            IOUtils.closeQuietly(sharedStream);
         }
         releaseResources();
      }

      @Override
      public synchronized boolean cancel()
      {
         completed = true;
         if (future != null) future.cancelledResult();
         if (sharedStream != null)
         {
            sharedStream.setException(new IOException("cancelled"));
            IOUtils.closeQuietly(sharedStream);
         }
         releaseResources();
         return true;
      }

      private static class ResultFuture<T> extends BasicFuture<T>
      {
         private final Future<T> httpFuture;

         public ResultFuture(final Future<T> httpFuture)
         {
            super(null);
            this.httpFuture = httpFuture;
         }

         @Override
         public boolean cancel(boolean mayInterruptIfRunning)
         {
            boolean cancelled = super.cancel(mayInterruptIfRunning);
            httpFuture.cancel(mayInterruptIfRunning);
            return cancelled;
         }

         public void cancelledResult() {
            super.cancel(true);
         }

         public void copyHttpFutureResult()
         {
            if (!isDone() && httpFuture.isDone())
            {
               try
               {
                  completed(httpFuture.get());
               }
               catch(ExecutionException e)
               {
                  failed(clientException(e.getCause(), null));
               }
               catch (InterruptedException e)
               { // cant happen because already isDone
                  failed(e);
               }
            }
         }
      }

      private class SharedInputStream extends ContentInputStream {

         private final SharedInputBuffer sharedBuf;
         private volatile IOException ex;
         private volatile IOControl ioctrl;

         public SharedInputStream(SharedInputBuffer sharedBuf)
         {
            super(sharedBuf);
            this.sharedBuf = sharedBuf;
         }

         public void consumeContent(ContentDecoder decoder, IOControl ioctrl) throws IOException {
            if (ioctrl != null) this.ioctrl = ioctrl;
            sharedBuf.consumeContent(decoder, ioctrl);
         }

         @Override
         public void close() throws IOException
         {
            completed = true; // next isDone() cancels.

            // Workaround for deadlock: super.close() reads until no more data, but on cancellation no more data is
            // pushed to consumeContent, thus deadlock. Instead notify the reactor by ioctrl.requestInput
            sharedBuf.close(); // next reads will return EndOfStream. Also wakes up any waiting readers
            IOControl ioctrl = this.ioctrl;
            if (ioctrl != null) ioctrl.requestInput(); // notify reactor to check isDone()
            super.close(); // does basically nothing due to closed buf
         }

         @Override
         public int read(final byte[] b, final int off, final int len) throws IOException
         {
            throwIfError();
            return super.read(b, off, len);
         }

         @Override
         public int read(final byte[] b) throws IOException
         {
            throwIfError();
            return super.read(b, 0, b.length);
         }

         @Override
         public int read() throws IOException {
            throwIfError();
            return super.read();
         }

         private void throwIfError() throws IOException {
            IOException ex = this.ex;
            if (ex != null) {
               //create a new exception here to make it easy figuring out where the offending blocking IO comes from
               throw new IOException(ex);
            }
         }

         public void setException(IOException e) {
            this.ex = e;
         }
      }
   }

   /**
    * Buffers response fully in memory.
    *
    * (Buffering is definitely easier to implement than streaming)
    */
   private static class BufferingResponseConsumer<T> extends AbstractAsyncResponseConsumer<T>
   {

      private ClientConfiguration configuration;
      private Map<String, Object> properties;
      private ResultExtractor<T> responseExtractor;
      private ConnectionResponse clientResponse;
      private SimpleInputBuffer buf;

      public BufferingResponseConsumer(ClientInvocation request, ResultExtractor<T> responseExtractor)
      {
         this.configuration = request.getClientConfiguration();
         this.properties = request.getMutableProperties();
         this.responseExtractor = responseExtractor;
      }

      @Override
      protected void onResponseReceived(HttpResponse response) throws HttpException, IOException
      {
         ConnectionResponse clientResponse = new ConnectionResponse(configuration, properties);
         copyResponse(response, clientResponse);
         final HttpEntity entity = response.getEntity();
         if (entity != null)
         {
            long len = entity.getContentLength();
            if (len > Integer.MAX_VALUE)
            {
               throw new ContentTooLongException("Entity content is too long: " + len);
            }
            if (len < 0)
            {
               len = 4096;
            }
            this.buf = new SimpleInputBuffer((int) len, new HeapByteBufferAllocator());
         }
         this.clientResponse = clientResponse;
      }

      @Override
      protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException
      {
      }

      @Override
      protected void onContentReceived(ContentDecoder decoder, IOControl ioctrl) throws IOException
      {
         SimpleInputBuffer buf = this.buf;
         if (buf == null) throw new NullPointerException("Content Buffer");
         buf.consumeContent(decoder);
      }

      @Override
      protected T buildResult(HttpContext context) throws Exception
      {
         if (buf != null) clientResponse.setConnection(new ContentInputStream(buf));
         return responseExtractor.extractResult(clientResponse);
      }

      @Override
      protected void releaseResources()
      {
         this.configuration = null;
         this.properties = null;
         this.responseExtractor = null;
         this.clientResponse = null;
         this.buf = null;
      }
   }

   /**
    * Adapter from http-FutureCallback<T> to InvocationCallback<T>
    */
   private static class CallbackAdapter<T> implements FutureCallback<T>
   {
      private final InvocationCallback<T> invocationCallback;

      public CallbackAdapter(InvocationCallback<T> invocationCallback)
      {
         this.invocationCallback = invocationCallback;
      }

      @Override
      public void cancelled()
      {
         invocationCallback.failed(new ProcessingException("cancelled"));
      }

      @Override
      public void completed(T response)
      {
         try
         {
            invocationCallback.completed(response);
         }
         catch (Throwable t)
         {
            LogMessages.LOGGER.exceptionIgnored(t);
         }
         finally
         {
            // just to promote proper callback usage, because HttpAsyncClient is responsible
            // for cleaning up the (buffered) connection
            if (response instanceof Response)
            {
               ((Response) response).close();
            }
         }
      }

      @Override
      public void failed(Exception ex)
      {
         invocationCallback.failed(clientException(ex, null));
      }
   }

   /**
    * ClientResponse with surefire releaseConnection
    */
   private static class ConnectionResponse extends ClientResponse
   {

      private InputStream connection;
      private InputStream stream;

      public ConnectionResponse(ClientConfiguration configuration, Map<String, Object> properties)
      {
         super(configuration);
         setProperties(properties);
      }

      public synchronized void setConnection(InputStream connection)
      {
         this.connection = connection;
         this.stream = connection;
      }

      @Override
      protected synchronized void setInputStream(InputStream is)
      {
         stream = is;
         resetEntity();
      }

      @Override
      public synchronized InputStream getInputStream()
      {
         return stream;
      }

      @Override
      public synchronized void releaseConnection() throws IOException
      {
         boolean thrown = true;
         try
         {
            if (stream != null) stream.close();
            thrown = false;
         }
         finally
         {
            if (connection != null)
            {
               if (thrown)
               {
                  IOUtils.closeQuietly(connection);
               }
               else
               {
                  connection.close();
               }
            }
         }
      }
   }

   private static class EndOfStream implements ContentDecoder
   {
      public static EndOfStream INSTANCE = new EndOfStream();

      @Override
      public int read(ByteBuffer dst) throws IOException
      {
         return -1;
      }

      @Override
      public boolean isCompleted()
      {
         return true;
      }
   }

   private static HttpUriRequest buildHttpRequest(ClientInvocation request)
   {
      // Writers may change headers. Thus buffer the content before committing the headers.
      // For simplicity's sake the content is buffered in memory. File-buffering (ZeroCopyConsumer...) would be
      // possible, but resource management is error-prone.

      HttpRequestBase httpRequest = createHttpMethod(request.getUri(), request.getMethod());
      if (request.getEntity() != null)
      {
         byte[] requestContent = requestContent(request);
         ByteArrayEntity entity = new ByteArrayEntity(requestContent);
         entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, request.getHeaders().getMediaType().toString()));
         commitHeaders(request, httpRequest);
         ((HttpEntityEnclosingRequest) httpRequest).setEntity(entity);
      }
      else
      {
         commitHeaders(request, httpRequest);
      }

      return httpRequest;
   }

   private static byte[] requestContent(ClientInvocation request)
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      request.getDelegatingOutputStream().setDelegate(baos);
      try
      {
         request.writeRequestBody(request.getEntityStream());
         baos.close();
         return baos.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static HttpRequestBase createHttpMethod(URI url, String restVerb)
   {
      if ("GET".equals(restVerb))
      {
         return new HttpGet(url);
      }
      else if ("POST".equals(restVerb))
      {
         return new HttpPost(url);
      }
      else
      {
         final String verb = restVerb;
         return new HttpPost(url)
         {
            @Override
            public String getMethod()
            {
               return verb;
            }
         };
      }
   }

   private static void commitHeaders(ClientInvocation request, HttpRequestBase httpMethod)
   {
      MultivaluedMap<String, String> headers = request.getHeaders().asMap();
      for (Map.Entry<String, List<String>> header : headers.entrySet())
      {
         List<String> values = header.getValue();
         for (String value : values)
         {
            httpMethod.addHeader(header.getKey(), value);
         }
      }
   }

   private static void copyResponse(HttpResponse httpResponse, ClientResponse clientResponse)
   {
      clientResponse.setStatus(httpResponse.getStatusLine().getStatusCode());
      CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();
      for (Header header : httpResponse.getAllHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }
      clientResponse.setHeaders(headers);
   }

   private static RuntimeException clientException(Throwable ex, Response clientResponse) {
      RuntimeException ret;
      if (ex == null)
      {
         ret = new ProcessingException(new NullPointerException());
      }
      else if (ex instanceof WebApplicationException)
      {
         ret = (WebApplicationException) ex;
      }
      else if (ex instanceof ProcessingException)
      {
         ret = (ProcessingException) ex;
      }
      else if (clientResponse != null)
      {
         ret = new ResponseProcessingException(clientResponse, ex);
      }
      else
      {
         ret = new ProcessingException(ex);
      }
      return ret;
   }

   private static IOException ioException(Exception ex) {
      return (ex instanceof IOException) ? (IOException) ex : new IOException(ex);
   }

}
