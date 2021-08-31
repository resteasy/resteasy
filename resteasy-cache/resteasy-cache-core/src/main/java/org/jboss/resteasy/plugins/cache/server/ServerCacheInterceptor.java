package org.jboss.resteasy.plugins.cache.server;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;
import org.jboss.resteasy.spi.BlockingAsyncOutputStream;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.NoLogWebApplicationException;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
public class ServerCacheInterceptor implements WriterInterceptor, AsyncWriterInterceptor
{
   protected ServerCache cache;

   public ServerCacheInterceptor(final ServerCache cache)
   {
      this.cache = cache;
   }

   @Context
   protected HttpRequest request;

   @Context
   protected Request validation;


   private static final String[] pseudo = {"0", "1", "2",
      "3", "4", "5", "6", "7", "8",
      "9", "A", "B", "C", "D", "E",
      "F"};

   public static String byteArrayToHexString(byte[] bytes)
   {

      byte ch = 0x00;

      StringBuffer out = new StringBuffer(bytes.length * 2);

      for (byte b : bytes)
      {

         ch = (byte) (b & 0xF0);
         ch = (byte) (ch >>> 4);
         ch = (byte) (ch & 0x0F);
         out.append(pseudo[(int) ch]);
         ch = (byte) (b & 0x0F);
         out.append(pseudo[(int) ch]);

      }

      String rslt = new String(out);

      return rslt;

   }

   protected String createHash(byte[] entity)
   {
      try
      {
         MessageDigest messagedigest = MessageDigest.getInstance("MD5");
         byte[] abyte0 = messagedigest.digest(entity);
         return byteArrayToHexString(abyte0);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
   }

   private CacheControl getCacheControl(MultivaluedMap<String, Object> headers) {
       if (!request.getHttpMethod().equalsIgnoreCase("GET") || request.getAttribute(ServerCacheHitFilter.DO_NOT_CACHE_RESPONSE) != null)
       {
          return null;
       }

       Object occ = headers.getFirst(HttpHeaders.CACHE_CONTROL);
       if (occ == null)
       {
          return null;
       }
       CacheControl cc = null;

       if (occ instanceof CacheControl) cc = (CacheControl) occ;
       else
       {
          cc = CacheControl.valueOf(occ.toString());
       }

       if (cc.isNoCache())
       {
          return null;
       }
       return cc;
   }

   private byte[] handleCaching(ByteArrayOutputStream buffer, CacheControl cc, MultivaluedMap<String, Object> headers, MediaType mediaType) {
       byte[] entity = buffer.toByteArray();
       Object etagObject = headers.getFirst(HttpHeaders.ETAG);
       String etag = null;
       if (etagObject == null)
       {
          etag = createHash(entity);
          headers.putSingle(HttpHeaders.ETAG, etag);
       }
       else // use application provided ETag if it exists
       {
          etag = etagObject.toString();
       }

       if (!cc.isPrivate() && !cc.isNoStore()) {
          MultivaluedMap<String, String> varyHeaders = new MultivaluedHashMap<>();
          if (headers.containsKey(HttpHeaders.VARY)) {
             for (Object varyHeader : headers.get(HttpHeaders.VARY)) {
                if (request.getMutableHeaders().containsKey(varyHeader)) {
                   varyHeaders.addAll((String) varyHeader, request.getMutableHeaders().get(varyHeader));
                }
             }
          }
          cache.add(request.getUri().getRequestUri().toString(), mediaType, cc, headers, entity, etag, varyHeaders);
       }

       // check to see if ETags are the same.  If they are, we don't need to send a response back.
       Response.ResponseBuilder validatedResponse = validation.evaluatePreconditions(new EntityTag(etag));
       if (validatedResponse != null)
       {
          throw new NoLogWebApplicationException(validatedResponse.status(Response.Status.NOT_MODIFIED).cacheControl(cc).header(HttpHeaders.ETAG, etag).build());
       }
       return entity;
   }

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());

      CacheControl cc = getCacheControl(context.getHeaders());
      if(cc == null) {
          context.proceed();
          return;
      }

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      OutputStream old = context.getOutputStream();
      try
      {
         context.setOutputStream(buffer);
         context.proceed();

         byte[] entity = handleCaching(buffer, cc, context.getHeaders(), context.getMediaType());
         old.write(entity);
      }
      finally
      {
         context.setOutputStream(old);
      }
   }

   @Override
   public CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context) {
       LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());

       CacheControl cc = getCacheControl(context.getHeaders());
       if(cc == null) {
           return context.asyncProceed();
       }

       ByteArrayOutputStream buffer = new ByteArrayOutputStream();
       AsyncOutputStream old = context.getAsyncOutputStream();
       context.setAsyncOutputStream(new BlockingAsyncOutputStream(buffer));
       return context.asyncProceed()
               .thenCompose(v -> {
                   byte[] entity = handleCaching(buffer, cc, context.getHeaders(), context.getMediaType());
                   return old.asyncWrite(entity);
               }).whenComplete((v, t) -> context.setAsyncOutputStream(old));
   }
}
