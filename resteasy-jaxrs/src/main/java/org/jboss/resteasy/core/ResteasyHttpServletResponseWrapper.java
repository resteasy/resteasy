package org.jboss.resteasy.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * ResteasyHttpServletResponseWrapper is introduced to support the use of
 * RequestDispatcher.forward() and RequestDispatcher.include(), which need
 * to be able to retrieve the orginal HttpServletResponse.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 18, 2014
 */
public class ResteasyHttpServletResponseWrapper extends ServletResponseWrapper implements HttpServletResponse
{
   private HttpServletResponse response;
   private HttpServletResponse proxy;
   
   public ResteasyHttpServletResponseWrapper(HttpServletResponse response, HttpServletResponse proxy)
   {
      super(response);
      this.proxy = proxy;
      this.response = response;
   }
   
   public HttpServletResponse getHttpServletResponse()
   {
      return response;
   }  
   
   @Override
   public ServletResponse getResponse()
   {
      return response;
   } 
   
   @Override
   public String getCharacterEncoding()
   {
      return proxy.getCharacterEncoding();
   }

   @Override
   public String getContentType()
   {
      return proxy.getContentType();
   }

   @Override
   public ServletOutputStream getOutputStream() throws IOException
   {
      return proxy.getOutputStream();
   }

   @Override
   public PrintWriter getWriter() throws IOException
   {
      return proxy.getWriter();
   }

   @Override
   public void setCharacterEncoding(String charset)
   {
      proxy.setCharacterEncoding(charset);
   }

   @Override
   public void setContentLength(int len)
   {
      proxy.setContentLength(len);
   }

   @Override
   public void setContentType(String type)
   {
      proxy.setContentType(type);
   }

   @Override
   public void setBufferSize(int size)
   {
      proxy.setBufferSize(size);
   }

   @Override
   public int getBufferSize()
   {
      return proxy.getBufferSize();
   }

   @Override
   public void flushBuffer() throws IOException
   {
      proxy.flushBuffer();
   }

   @Override
   public void resetBuffer()
   {
      proxy.resetBuffer();
   }

   @Override
   public boolean isCommitted()
   {
      return proxy.isCommitted();
   }

   @Override
   public void reset()
   {
      proxy.reset();
   }

   @Override
   public void setLocale(Locale loc)
   {
      proxy.setLocale(loc);
   }

   @Override
   public Locale getLocale()
   {
      return proxy.getLocale();
   }

   @Override
   public void addCookie(Cookie cookie)
   {
      proxy.addCookie(cookie);
   }

   @Override
   public boolean containsHeader(String name)
   {
      return proxy.containsHeader(name);
   }

   @Override
   public String encodeURL(String url)
   {
      return proxy.encodeURL(url);
   }

   @Override
   public String encodeRedirectURL(String url)
   {
      return proxy.encodeRedirectURL(url);
   }

   /**
    * @deprecated   As of version 2.1, use encodeURL(String url) instead
    *
    * @param  url   the url to be encoded.
    * @return    the encoded URL if encoding is needed; 
    *         the unchanged URL otherwise.
    */
   @Override
   public String encodeUrl(String url)
   {
      return proxy.encodeUrl(url);
   }

   /**
    * @deprecated   As of version 2.1, use 
    *         encodeRedirectURL(String url) instead
    *
    * @param  url   the url to be encoded.
    * @return    the encoded URL if encoding is needed; 
    *         the unchanged URL otherwise.
    */
   @Override
   public String encodeRedirectUrl(String url)
   {
      return proxy.encodeRedirectUrl(url);
   }

   @Override
   public void sendError(int sc, String msg) throws IOException
   {
      proxy.sendError(sc, msg);
   }

   @Override
   public void sendError(int sc) throws IOException
   {
      proxy.sendError(sc);
   }

   @Override
   public void sendRedirect(String location) throws IOException
   {
      proxy.sendRedirect(location);
   }

   @Override
   public void setDateHeader(String name, long date)
   {
      proxy.setDateHeader(name, date);
   }

   @Override
   public void addDateHeader(String name, long date)
   {
      proxy.addDateHeader(name, date);
   }

   @Override
   public void setHeader(String name, String value)
   {
      proxy.setHeader(name, value);
   }

   @Override
   public void addHeader(String name, String value)
   {
      proxy.addHeader(name, value);
   }

   @Override
   public void setIntHeader(String name, int value)
   {
      proxy.setIntHeader(name, value);
   }

   @Override
   public void addIntHeader(String name, int value)
   {
      proxy.addIntHeader(name, value);
   }

   @Override
   public void setStatus(int sc)
   {
      proxy.setStatus(sc);
   }

   /**
    * @deprecated As of version 2.1, due to ambiguous meaning of the 
    * message parameter. To set a status code 
    * use <code>setStatus(int)</code>, to send an error with a description
    * use <code>sendError(int, String)</code>.
    *
    * Sets the status code and message for this response.
    * 
    * @param  sc the status code
    * @param  sm the status message
    */
   @Override
   public void setStatus(int sc, String sm)
   {
      proxy.setStatus(sc, sm);
   }

   @Override
   public int getStatus()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public String getHeader(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Collection<String> getHeaders(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Collection<String> getHeaderNames()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
}
