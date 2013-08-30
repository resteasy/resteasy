package com.restfully.shop.features;

import org.jboss.resteasy.util.Base64;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContentMD5Writer implements WriterInterceptor
{
   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      OutputStream old = context.getOutputStream();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      MessageDigest digest = null;
      try
      {
         digest = MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new IllegalArgumentException(e);
      }
      DigestOutputStream digestStream = new DigestOutputStream(buffer, digest);
      context.setOutputStream(digestStream);

      context.proceed();

      byte[] hash = digest.digest();
      String encodedHash = Base64.encodeBytes(hash);
      context.getHeaders().putSingle("Content-MD5", encodedHash);

      context.setOutputStream(old);
      byte[] content = buffer.toByteArray();
      old.write(content);
   }
}
