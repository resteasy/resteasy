package org.jboss.resteasy.mock;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
   protected int status;
   protected ByteArrayOutputStream baos = new ByteArrayOutputStream();
   protected CaseInsensitiveMap outputHeaders = new CaseInsensitiveMap();
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
      return baos;
   }

   public byte[] getOutput()
   {
      return baos.toByteArray();
   }

   public String getContentAsString()
   {
      return new String(baos.toByteArray());
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
      outputHeaders = new CaseInsensitiveMap();
      newCookies = new ArrayList<NewCookie>();
      sentError = false;
      status = 0;
      errorMessage = null;
   }
}