package org.jboss.resteasy.plugins.cache.server;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.NoLogWebApplicationException;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
public class ServerCacheInterceptor implements WriterInterceptor
{
   protected ServerCache cache;

   public ServerCacheInterceptor(ServerCache cache)
   {
      this.cache = cache;
   }

   @Context
   protected HttpRequest request;

   @Context
   protected Request validation;


   private static final String pseudo[] = {"0", "1", "2",
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
         byte abyte0[] = messagedigest.digest(entity);
         return byteArrayToHexString(abyte0);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
       LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());

      if (!request.getHttpMethod().equalsIgnoreCase("GET") || request.getAttribute(ServerCacheHitFilter.DO_NOT_CACHE_RESPONSE) != null)
      {
         context.proceed();
         return;
      }

      Object occ = context.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
      if (occ == null)
      {
         context.proceed();
         return;
      }
      CacheControl cc = null;

      if (occ instanceof CacheControl) cc = (CacheControl) occ;
      else
      {
         cc = CacheControl.valueOf(occ.toString());
      }

      if (cc.isNoCache())
      {
         context.proceed();
         return;
      }

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      OutputStream old = context.getOutputStream();
      try
      {
         context.setOutputStream(buffer);
         context.proceed();
         byte[] entity = buffer.toByteArray();
         Object etagObject = context.getHeaders().getFirst(HttpHeaders.ETAG);
         String etag = null;
         if (etagObject == null)
         {
            etag = createHash(entity);
            context.getHeaders().putSingle(HttpHeaders.ETAG, etag);
         }
         else // use application provided ETag if it exists
         {
            etag = etagObject.toString();
         }
         
         if (!cc.isPrivate() && !cc.isNoStore()) {
             MultivaluedMap<String, String> varyHeaders = new MultivaluedHashMap<>();
             if (context.getHeaders().containsKey(HttpHeaders.VARY)) {
                 for (Object varyHeader : context.getHeaders().get(HttpHeaders.VARY)) {
                     if (request.getMutableHeaders().containsKey(varyHeader)) {
                         varyHeaders.addAll((String) varyHeader, request.getMutableHeaders().get(varyHeader));
                     }
                 }
             }
             cache.add(request.getUri().getRequestUri().toString(), context.getMediaType(), cc, context.getHeaders(), entity, etag, varyHeaders);
         }

         // check to see if ETags are the same.  If they are, we don't need to send a response back.
         Response.ResponseBuilder validatedResponse = validation.evaluatePreconditions(new EntityTag(etag));
         if (validatedResponse != null)
         {
            throw new NoLogWebApplicationException(validatedResponse.status(Response.Status.NOT_MODIFIED).cacheControl(cc).header(HttpHeaders.ETAG, etag).build());
         }

         old.write(entity);
      }
      finally
      {
         context.setOutputStream(old);
      }

   }
}
