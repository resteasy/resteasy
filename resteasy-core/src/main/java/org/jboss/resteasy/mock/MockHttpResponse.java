package org.jboss.resteasy.mock;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a bridge between asynchronous message and reply
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockHttpResponse implements HttpResponse
{
   private static final String CHARSET_PREFIX = "charset=";
   protected int status;
   protected ByteArrayOutputStream baos = new ByteArrayOutputStream();
   protected OutputStream os = baos;
   protected CaseInsensitiveMap<Object> outputHeaders = new CaseInsensitiveMap<Object>();
   protected List<NewCookie> newCookies = new ArrayList<NewCookie>();
   protected String errorMessage;
   protected boolean sentError = false;

   public MockHttpResponse()
   {
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return outputHeaders;
   }

   public OutputStream getOutputStream() throws IOException
   {
      return os;
   }

   @Override
   public void setOutputStream(OutputStream os)
   {
      this.os = os;
   }

   public byte[] getOutput()
   {
      return baos.toByteArray();
   }

   public String getContentAsString() throws UnsupportedEncodingException
   {
      String charset = getCharset();
      return (charset == null ? baos.toString() : baos.toString(charset));
   }

   private String getCharset()
   {
      String characterEncoding = null;
      MultivaluedMap<String, Object> headers = this.getOutputHeaders();
      Object obj = headers.getFirst(HttpHeaderNames.CONTENT_TYPE);
      String value = null;
      if (obj instanceof MediaType)
      {
         value = ((MediaType)obj).toString();
      }
      else
      {
         value = (String)obj;
      }

      if (value != null && !value.isEmpty())
      {
         int charsetIndex = value.toLowerCase().indexOf(CHARSET_PREFIX);
         if (charsetIndex != -1)
         {
            characterEncoding = value.substring(charsetIndex + CHARSET_PREFIX.length());
         }
      }
      return characterEncoding;
   }

   public void addNewCookie(NewCookie cookie)
   {
      newCookies.add(cookie);
   }

   public void sendError(int status) throws IOException
   {
      sentError = true;
      this.status = status;
   }

   public void sendError(int status, String message) throws IOException
   {
      sentError = true;
      this.status = status;
      this.errorMessage = message;
   }

   public List<NewCookie> getNewCookies()
   {
      return newCookies;
   }

   public String getErrorMessage()
   {
      return errorMessage;
   }

   public boolean isErrorSent()
   {
      return sentError;
   }

   public boolean isCommitted()
   {
      return baos.size() > 0;
   }

   public void reset()
   {
      baos = new ByteArrayOutputStream();
      os = baos;
      outputHeaders = new CaseInsensitiveMap<Object>();
      newCookies = new ArrayList<NewCookie>();
      sentError = false;
      status = 0;
      errorMessage = null;
   }

   @Override
   public void flushBuffer() throws IOException
   {
   }
}