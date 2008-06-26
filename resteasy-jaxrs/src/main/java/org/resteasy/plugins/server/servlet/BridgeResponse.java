package org.resteasy.plugins.server.servlet;

import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.specimpl.MultivaluedMapImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * Acts as a bridge between asynchronous message and reply
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BridgeResponse implements HttpResponse
{
   private int status = 200;
   private ByteArrayOutputStream baos = new ByteArrayOutputStream();
   private MultivaluedMap<String, Object> outputHeaders = new MultivaluedMapImpl<String, Object>();
   private List<NewCookie> newCookies = new ArrayList<NewCookie>();
   private String errorMessage;
   private boolean sentError = false;

   public BridgeResponse()
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
}