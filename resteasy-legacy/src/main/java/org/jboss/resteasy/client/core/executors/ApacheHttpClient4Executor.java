package org.jboss.resteasy.client.core.executors;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse.BaseClientResponseStreamFactory;
import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.jboss.resteasy.client.exception.mapper.ApacheHttpClient4ExceptionMapper;
import org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApacheHttpClient4Executor implements ClientExecutor
{
   public static final String BYTE_MEMORY_UNIT = "BY";
   public static final String KILOBYTE_MEMORY_UNIT = "KB";
   public static final String MEGABYTE_MEMORY_UNIT = "MB";
   public static final String GIGABYTE_MEMORY_UNIT = "GB";

   /**
    * Used to build temp file prefix.
    */
   private static String processId = null;

   static
   {
      ApacheHttpClient4Executor.processId = ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^0-9a-zA-Z]", "");
   }

   static synchronized private void checkClientExceptionMapper()
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      if (factory instanceof org.jboss.resteasy.spi.old.ResteasyProviderFactory)
      {
         org.jboss.resteasy.spi.old.ResteasyProviderFactory f = (org.jboss.resteasy.spi.old.ResteasyProviderFactory)factory;
         if (f.getClientExceptionMapper(Exception.class) == null)
         {
            Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(ApacheHttpClient4ExceptionMapper.class,
                  ClientExceptionMapper.class)[0];
            f.addClientExceptionMapper(new ApacheHttpClient4ExceptionMapper(), exceptionType);
         }
      }
   }

   protected HttpClient httpClient;
   protected boolean createdHttpClient;
   protected HttpContext httpContext;
   protected boolean closed;

   /**
    * For uploading File's over JAX-RS framework, this property, together with {@link #fileUploadMemoryUnit},
    * defines the maximum File size allowed in memory. If fileSize exceeds this size, it will be stored to
    * {@link #fileUploadTempFileDir}. <br>
    * <br>
    * Defaults to 1 MB
    */
   private int fileUploadInMemoryThresholdLimit = 1;

   /**
    * The unit for {@link #fileUploadInMemoryThresholdLimit}. <br>
    * <br>
    * Defaults to MB.
    *
    * @see MemoryUnit
    */
   private MemoryUnit fileUploadMemoryUnit = MemoryUnit.MB;

   /**
    * Temp directory to write output request stream to. Any file to be uploaded has to be written out to the
    * output request stream to be sent to the service and when the File is too huge the output request stream is
    * written out to the disk rather than to memory. <br>
    * <br>
    * Defaults to JVM temp directory.
    */
   private File fileUploadTempFileDir = new File(System.getProperty("java.io.tmpdir"));

   protected int responseBufferSize = 8192;

   public ApacheHttpClient4Executor()
   {
      this(new DefaultHttpClient(), null);
      this.createdHttpClient = true;
   }

   public ApacheHttpClient4Executor(HttpClient httpClient)
   {
      this(httpClient, null);
   }

   public ApacheHttpClient4Executor(HttpClient httpClient, HttpContext httpContext)
   {
      this.httpClient = httpClient;
      this.httpContext = httpContext;
      checkClientExceptionMapper();
   }

   /**
    * Response stream is wrapped in a BufferedInputStream.  Default is 8192.  Value of 0 will not wrap it.
    * Value of -1 will use a SelfExpandingBufferedInputStream.
    *
    *  @return response buffer size
    */
   public int getResponseBufferSize()
   {
       return responseBufferSize;
   }

   /**
    * Response stream is wrapped in a BufferedInputStream.  Default is 8192.  Value of 0 will not wrap it.
    * Value of -1 will use a SelfExpandingBufferedInputStream.
    *
    * @param responseBufferSize response buffer size
    */
   public void setResponseBufferSize(int responseBufferSize)
   {
       this.responseBufferSize = responseBufferSize;
   }

   public HttpClient getHttpClient()
   {
      return httpClient;
   }

   public HttpContext getHttpContext()
   {
      return httpContext;
   }

   public void setHttpContext(HttpContext httpContext)
   {
      this.httpContext = httpContext;
   }

   public static CaseInsensitiveMap<String> extractHeaders(
           HttpResponse response)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();

      for (Header header : response.getAllHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }
      return headers;
   }

   public ClientRequest createRequest(String uriTemplate)
   {
      return new ClientRequest(uriTemplate, this);
   }

   public ClientRequest createRequest(UriBuilder uriBuilder)
   {
      return new ClientRequest(uriBuilder, this);
   }

   static class ResponseStream extends SelfExpandingBufferredInputStream
   {
      BaseClientResponse response;

      public ResponseStream(InputStream in, BaseClientResponse response)
      {
         super(in);
         // Keep a reference to the response object to prevent it being finalized prematurely
         this.response = response;
      }

      public synchronized void close() throws IOException
      {
         super.close();
         // Response object is no longer needed and can be finalized
         response = null;
      }
   }

   protected InputStream createBufferedStream(InputStream is)
   {
      if (responseBufferSize == 0)
      {
         return is;
      }
      if (responseBufferSize < 0)
      {
         return new SelfExpandingBufferredInputStream(is);
      }
      BufferedInputStream bis = new BufferedInputStream(is, responseBufferSize);
      // mark read limit
      bis.mark(responseBufferSize);
      return bis;
   }

   @SuppressWarnings("unchecked")
   public ClientResponse execute(ClientRequest request) throws Exception
   {
      String uri = request.getUri();
      final HttpRequestBase httpMethod = createHttpMethod(uri, request.getHttpMethod());
      try
      {
         loadHttpMethod(request, httpMethod);

         final HttpResponse res = httpClient.execute(httpMethod, httpContext);

         final BaseClientResponse response = new BaseClientResponse(null, this);
         BaseClientResponseStreamFactory sf = new BaseClientResponseStreamFactory()
         {
            InputStream stream;

            public InputStream getInputStream() throws IOException
            {
               if (stream == null)
               {
                  HttpEntity entity = res.getEntity();
                  if (entity == null) return null;
                  // stream = new SelfExpandingBufferredInputStream(entity.getContent());
                  stream = createBufferedStream(entity.getContent());
               }
               return stream;
            }

            public void performReleaseConnection()
            {
               // Apache Client 4 is stupid, You have to get the InputStream and close it if there is an entity
               // otherwise the connection is never released. There is, of course, no close() method on response
               // to make this easier.
               try
               {
                  if (stream != null)
                  {
                     stream.close();
                  }
                  else
                  {
                     InputStream is = getInputStream();
                     if (is != null)
                     {
                        is.close();
                     }
                  }
               }
               catch (Exception ignore)
               {
               }
            }
         };
         response.setStreamFactory(sf);
         response.setAttributes(request.getAttributes());
         response.setStatus(res.getStatusLine().getStatusCode());
         response.setHeaders(extractHeaders(res));
         response.setProviderFactory(request.getProviderFactory());
         return response;
      }
      finally
      {
         cleanUpAfterExecute(httpMethod);
      }
   }

   /**
    * If passed httpMethod is of type HttpPost then obtain its entity. If the entity has an enclosing File then
    * delete it by invoking this method after the request has completed. The entity will have an enclosing File
    * only if it was too huge to fit into memory.
    *
    * @param httpMethod - the httpMethod to clean up.
    * @see #writeRequestBodyToOutputStream(ClientRequest)
    */
   protected void cleanUpAfterExecute(final HttpRequestBase httpMethod)
   {
      if (httpMethod != null && httpMethod instanceof HttpPost)
      {
         HttpPost postMethod = (HttpPost) httpMethod;
         HttpEntity entity = postMethod.getEntity();
         if (entity != null && entity instanceof FileExposingFileEntity)
         {
            File tempRequestFile = ((FileExposingFileEntity) entity).getFile();
            try
            {
               boolean isDeleted = tempRequestFile.delete();
               if (!isDeleted)
               {
                  handleFileNotDeletedError(tempRequestFile, null);
               }
            }
            catch (Exception ex)
            {
               handleFileNotDeletedError(tempRequestFile, ex);
            }
         }
      }
   }

   private HttpRequestBase createHttpMethod(String url, String restVerb)
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

   public void loadHttpMethod(final ClientRequest request, HttpRequestBase httpMethod) throws Exception
   {
      if (httpMethod instanceof HttpGet && request.followRedirects())
      {
         HttpClientParams.setRedirecting(httpMethod.getParams(), true);
      }
      else
      {
         HttpClientParams.setRedirecting(httpMethod.getParams(), false);
      }

      if (request.getBody() != null && !request.getFormParameters().isEmpty())
         throw new RuntimeException(Messages.MESSAGES.cannotSendFormParametersAndEntity());

      if (!request.getFormParameters().isEmpty())
      {
         commitHeaders(request, httpMethod);
         HttpPost post = (HttpPost) httpMethod;

         List<NameValuePair> formparams = new ArrayList<NameValuePair>();

         for (Map.Entry<String, List<String>> formParam : request.getFormParameters().entrySet())
         {
            List<String> values = formParam.getValue();
            for (String value : values)
            {
               formparams.add(new BasicNameValuePair(formParam.getKey(), value));
            }
         }

         UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
         post.setEntity(entity);
      }
      else if (request.getBody() != null)
      {
         if (httpMethod instanceof HttpGet) throw new RuntimeException(Messages.MESSAGES.getRequestCannotHaveBody());

         try
         {
            HttpEntity entity = buildEntity(request);
            HttpPost post = (HttpPost) httpMethod;
            commitHeaders(request, httpMethod);
            post.setEntity(entity);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else // no body
      {
         commitHeaders(request, httpMethod);
      }
   }

   /**
    * Build the HttpEntity to be sent to the Service as part of (POST) request. Creates a off-memory
    * {@link FileExposingFileEntity} or a regular in-memory {@link ByteArrayEntity} depending on if the request
    * OutputStream fit into memory when built by calling {@link #writeRequestBodyToOutputStream(ClientRequest)}.
    *
    * @param request -
    * @return - the built HttpEntity
    * @throws IOException -
    */
   protected HttpEntity buildEntity(final ClientRequest request) throws IOException
   {
      HttpEntity entityToBuild = null;
      DeferredFileOutputStream memoryManagedOutStream = writeRequestBodyToOutputStream(request);

      if (memoryManagedOutStream.isInMemory())
      {
         ByteArrayEntity entityToBuildByteArray = new ByteArrayEntity(memoryManagedOutStream.getData());
         entityToBuildByteArray.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, request.getBodyContentType().toString()));
         entityToBuild = entityToBuildByteArray;
      }
      else
      {
         entityToBuild = new FileExposingFileEntity(memoryManagedOutStream.getFile(), request.getBodyContentType().toString());
      }

      return entityToBuild;
   }

   /**
    * Creates the request OutputStream, to be sent to the end Service invoked, as a
    * <a href="http://commons.apache.org/io/api-release/org/apache/commons/io/output/DeferredFileOutputStream.html"
    * >DeferredFileOutputStream</a>.
    *
    * @param request -
    * @return - DeferredFileOutputStream with the ClientRequest written out per HTTP specification.
    * @throws IOException -
    */
   private DeferredFileOutputStream writeRequestBodyToOutputStream(final ClientRequest request) throws IOException
   {
      DeferredFileOutputStream memoryManagedOutStream =
              new DeferredFileOutputStream(this.fileUploadInMemoryThresholdLimit * getMemoryUnitMultiplier(),
                      getTempfilePrefix(), ".tmp", this.fileUploadTempFileDir);
      request.writeRequestBody(request.getHeadersAsObjects(), memoryManagedOutStream);
      memoryManagedOutStream.close();
      return memoryManagedOutStream;
   }

   /**
    * @return - the constant to multiply {@link #fileUploadInMemoryThresholdLimit} with based on
    *         {@link #fileUploadMemoryUnit} enumeration value.
    */
   private int getMemoryUnitMultiplier()
   {
      switch (this.fileUploadMemoryUnit)
      {
         case BY:
            return 1;
         case KB:
            return 1024;
         case MB:
            return 1024 * 1024;
         case GB:
            return 1024 * 1024 * 1024;
      }
      return 1;
   }

   /**
    * Use context information, which will include node name, to avoid conflicts in case of multiple VMS using same
    * temp directory location.
    *
    * @return -
    */
   protected String getTempfilePrefix()
   {
      return ApacheHttpClient4Executor.processId;
   }

   /**
    * Log that the file did not get deleted but prevent the request from failing by eating the exception.
    * Register the file to be deleted on exit, so it will get deleted eventually.
    *
    * @param tempRequestFile -
    * @param ex              - a null may be passed in which case this param gets ignored.
    */
   private void handleFileNotDeletedError(File tempRequestFile, Exception ex)
   {
      LogMessages.LOGGER.couldNotDeleteFile(tempRequestFile.getAbsolutePath(), ex);
      tempRequestFile.deleteOnExit();
   }

   /**
    * Setter for the {@link HttpClient} to which this class delegates the actual HTTP call. Note that this class
    * acts as the adapter between RestEasy and Apache HTTP Component library.
    *
    * @param pHttpClient -
    */
   void setHttpClient(HttpClient pHttpClient)
   {
      this.httpClient = pHttpClient;
   }

   /**
    * Setter for {@link #fileUploadInMemoryThresholdLimit}
    *
    * @param pInMemoryThresholdLimit - the inMemoryThresholdLimitMB to set
    */
   public void setFileUploadInMemoryThresholdLimit(int pInMemoryThresholdLimit)
   {
      this.fileUploadInMemoryThresholdLimit = pInMemoryThresholdLimit;
   }

   /**
    * Setter for {@link #fileUploadTempFileDir}
    *
    * @param pTempFileDir the tempFileDir to set
    */
   public void setFileUploadTempFileDir(File pTempFileDir)
   {
      this.fileUploadTempFileDir = pTempFileDir;
   }

   /**
    * Setter for {@link #fileUploadMemoryUnit}
    *
    * @param pMemoryUnit the memoryUnit to set
    */
   public void setFileUploadMemoryUnit(String pMemoryUnit)
   {
      this.fileUploadMemoryUnit = MemoryUnit.valueOf(pMemoryUnit);
   }

   public void commitHeaders(ClientRequest request, HttpRequestBase httpMethod)
   {
      MultivaluedMap<String, String> headers = request.getHeaders();
      for (Map.Entry<String, List<String>> header : headers.entrySet())
      {
         List<String> values = header.getValue();
         for (String value : values)
         {
//               System.out.println(String.format("setting %s = %s", header.getKey(), value));
            httpMethod.addHeader(header.getKey(), value);
         }
      }
   }

   public void close()
   {
      if (closed)
         return;

      if (createdHttpClient && httpClient != null)
      {
         ClientConnectionManager manager = httpClient.getConnectionManager();
         if (manager != null)
         {
            manager.shutdown();
         }
      }
      closed = true;
   }

   public boolean isClosed()
   {
      return closed;
   }

   public void finalize() throws Throwable
   {
      close();
      super.finalize();
   }

   /**
    * We use {@link FileEntity} as the {@link HttpEntity} implementation when the request OutputStream has been
    * saved to a File on disk (because it was too large to fit into memory see
    * {@link RestCFHttpClientExecutor#writeRequestBodyToOutputStream(ClientRequest)}); however, we have to delete
    * the File supporting the <code>FileEntity</code>, otherwise the disk will soon run out of space - remember
    * that there can be very huge files, in GB range, processed on a regular basis - and FileEntity exposes its
    * content File as a protected field. For the enclosing parent class ( {@link ApacheHttpClient4Executor} ) to be
    * able to get a handle to this content File and delete it, this class expose the content File.<br>
    * This class is private scoped to prevent access to this content File outside of the parent class.
    *
    * @author <a href="mailto:stikoo@digitalriver.com">Sandeep Tikoo</a>
    */
   private static class FileExposingFileEntity extends FileEntity
   {
      /**
       * @param pFile        -
       * @param pContentType -
       */
      public FileExposingFileEntity(File pFile, String pContentType)
      {
         super(pFile, pContentType);
      }

      /**
       * @return - the content File enclosed by this FileEntity.
       */
      File getFile()
      {
         return this.file;
      }
   }

   /**
    * Enumeration to represent memory units.
    */
   private static enum MemoryUnit
   {
      /**
       * Bytes
       */
      BY,
      /**
       * Killo Bytes
       */
      KB,

      /**
       * Mega Bytes
       */
      MB,

      /**
       * Giga Bytes
       */
      GB
   }
}