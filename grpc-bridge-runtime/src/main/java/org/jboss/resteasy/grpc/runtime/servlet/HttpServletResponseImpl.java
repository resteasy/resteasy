package org.jboss.resteasy.grpc.runtime.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jboss.resteasy.grpc.runtime.i18n.Messages;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessageV3;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

/**
 * Based on io.undertow.servlet.spec.HttpServletResponseImpl
 * by Stuart Douglas and Richard Opalka
 *
 * NOT IMPLEMENTED: Currently, methods related to URL encoding and redirection are not implemented.
 *
 */
public class HttpServletResponseImpl implements HttpServletResponse {

   public static final String GRPC_RETURN_RESPONSE = "grpc-return-response";
   public static final String GRPC_ASYNC = "grpc-async";

   public enum ResponseState {
      NONE,
      STREAM,
      WRITER
   }

   private static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
   private static final SimpleDateFormat SDF = new SimpleDateFormat(RFC1123_PATTERN, Locale.US);

   private GeneratedMessageV3.Builder<?> builder;
   private FieldDescriptor fd;
   private AsyncMockServletOutputStream msos = new AsyncMockServletOutputStream();
   private MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
   private String contentType;
   private String charset;
   private boolean charsetSet = false; //if a content type has been set either implicitly or implicitly
   private PrintWriter writer;
   private int statusCode;
   private Locale locale;
   private List<Cookie> cookies = new ArrayList<Cookie>();
   private ResponseState responseState = ResponseState.NONE;

   public HttpServletResponseImpl(final String retn, final String async, final ServletContext servletContext, final GeneratedMessageV3.Builder<?> builder, final FieldDescriptor fd) {
      if ("com.google.protobuf.Any".equals(retn) || "Any".equals(retn)) {
         List<String> list = new ArrayList<String>();
         list.add("true");
         headers.put(GRPC_RETURN_RESPONSE, list);
      }
      if ("async".equals(async)) {
         List<String> list = new ArrayList<String>();
         list.add("true");
         headers.put(GRPC_ASYNC, list);
      }
      this.builder = builder;
      this.fd = fd;
   }

   @Override
   public String getCharacterEncoding() {
      if (charset != null) {
         return charset;
      }
      // if no explicit encoding is specified, this method is supposed to return ISO-8859-1 as per the
      // expectation of this API
      return StandardCharsets.ISO_8859_1.name();
   }

   @Override
   public String getContentType() {
      if (contentType != null) {
         if (charsetSet) {
            return contentType + ";charset=" + getCharacterEncoding();
         } else {
            return contentType;
         }
      }
      return null;
   }

   @Override
   public ServletOutputStream getOutputStream() throws IOException {
      if (responseState == ResponseState.WRITER) {
         throw Messages.MESSAGES.getWriterAlreadyCalled();
      }
      responseState = ResponseState.STREAM;
      return msos;
   }

   @Override
   public PrintWriter getWriter() throws IOException {
      if (writer == null) {
         if (!charsetSet) {
            //servet 5.5
            setCharacterEncoding(getCharacterEncoding());
         }
         if (responseState == ResponseState.STREAM) {
            throw Messages.MESSAGES.getOutputStreamAlreadyCalled();
         }
         responseState = ResponseState.WRITER;
         writer = new PrintWriter(msos);
      }
      return writer;
   }

   @Override
   public void setCharacterEncoding(final String charset) {
      charsetSet = charset != null;
      this.charset = charset;
      if (contentType != null) {
         headers.put(HttpHeaders.CONTENT_TYPE, toList(getContentType()));
      }
   }

   @Override
   public void setContentLength(int len) {
   }

   @Override
   public void setContentLengthLong(long len) {
   }

   @Override
   public void setContentType(String type) {
      if (type == null) {
         return;
      }
      ContentTypeInfo ct = parseContentType(type);
      contentType = ct.getContentType();
      boolean useCharset = false;
      if(ct.getCharset() != null && writer == null && !isCommitted()) {
         charset = ct.getCharset();
         charsetSet = true;
         useCharset = true;
      }
      if(useCharset || !charsetSet) {
         headers.put(HttpHeaders.CONTENT_TYPE, toList(ct.getHeader()));
      } else if(ct.getCharset() == null) {
         headers.put(HttpHeaders.CONTENT_TYPE, toList(ct.getHeader() + "; charset=" + charset));
      }else {
         headers.put(HttpHeaders.CONTENT_TYPE, toList(ct.getContentType() + "; charset=" + charset));
      }
   }

   @Override
   public void setBufferSize(int size) {
      // no-op
   }

   @Override
   public int getBufferSize() {
      return 0;
   }

   @Override
   public void flushBuffer() throws IOException {
      // no-op
   }

   @Override
   public void resetBuffer() {
      // no-op
   }

   @Override
   public boolean isCommitted() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void reset() {
      msos.reset();
      writer = null;
      headers.clear();
      responseState = ResponseState.NONE;
      statusCode = Response.Status.OK.getStatusCode();
   }

   @Override
   public void setLocale(Locale loc) {
      this.locale = loc;
      headers.put(HttpHeaders.CONTENT_LANGUAGE, toList(loc.getLanguage() + "-" + loc.getCountry()));
   }

   @Override
   public Locale getLocale() {
      if (locale != null) {
         return locale;
      }
      return Locale.getDefault();
   }

   @Override
   public void addCookie(Cookie cookie) {
      cookies.add(cookie);
   }

   @Override
   public boolean containsHeader(String name) {
      return headers.containsKey(name);
   }

   @Override
   public String encodeURL(String url) {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("encodeURL()"));
   }

   @Override
   public String encodeRedirectURL(String url) {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("encodeRedirectURL()"));
   }

   @Override
   public String encodeUrl(String url) {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("encodeUrl()"));
   }

   @Override
   public String encodeRedirectUrl(String url) {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("encodeRedirectUrl()"));
   }

   @Override
   public void sendError(int sc, String msg) throws IOException {
      builder.setField(fd, "error status: " + sc + ": " + msg).build().writeTo(msos);
   }

   @Override
   public void sendError(int sc) throws IOException {
      builder.setField(fd, "error status: " + sc).build().writeTo(msos);
   }

   @Override
   public void sendRedirect(String location) throws IOException {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("sendRedirect()"));
   }

   @Override
   public void setDateHeader(String name, long date) {
      setHeader(name, toDateString(new Date(date)));
   }

   @Override
   public void addDateHeader(String name, long date) {
      addHeader(name, toDateString(new Date(date)));
   }

   @Override
   public void setHeader(String name, String value) {
      if(name == null) {
         throw Messages.MESSAGES.headerNameWasNull();
      }
      headers.put(name, toList(value));
   }

   @Override
   public void addHeader(String name, String value) {
      List<String> list = headers.get(name);
      if (list == null) {
         list = new ArrayList<String>();
      }
      list.add(value);
      headers.put(name, list);
   }

   @Override
   public void setIntHeader(String name, int value) {
      setHeader(name, Integer.toString(value));
   }

   @Override
   public void addIntHeader(String name, int value) {
      addHeader(name, Integer.toString(value));
   }

   @Override
   public void setStatus(int sc) {
      statusCode = sc;
   }

   @Override
   public void setStatus(int sc, String sm) {
      statusCode = sc;
   }

   @Override
   public int getStatus() {
      return statusCode;
   }

   @Override
   public String getHeader(String name) {
      List<String> list = headers.get(name);
      if (list == null || list.size() == 0) {
         return null;
      }
      return list.get(0);
   }

   @Override
   public Collection<String> getHeaders(String name) {
      return headers.get(name);
   }

   @Override
   public Collection<String> getHeaderNames() {
      return headers.keySet();
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////
   //////////////////////////////   public non override methods   /////////////////////////////////
   ////////////////////////////////////////////////////////////////////////////////////////////////
   public List<Cookie> getCookies() {
      return cookies;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////
   //////////////////////////////////////////   private   /////////////////////////////////////////
   ////////////////////////////////////////////////////////////////////////////////////////////////
   private List<String> toList(String s) {
      List<String> list = new ArrayList<String>();
      list.add(s);
      return list;
   }

   private ContentTypeInfo parseContentType(String type) {
      String contentType = type;
      String charset = null;

      int split = type.indexOf(";");
      if (split != -1) {
         int pos = type.indexOf("charset=");
         if (pos != -1) {
            int i = pos + "charset=".length();
            do {
               char c = type.charAt(i);
               if (c == ' ' || c == '\t' || c == ';') {
                  break;
               }
               ++i;
            } while (i < type.length());
            charset = type.substring(pos + "charset=".length(), i);
            //it is valid for the charset to be enclosed in quotes
            if (charset.startsWith("\"") && charset.endsWith("\"") && charset.length() > 1) {
               charset = charset.substring(1, charset.length() - 1);
            }

            int charsetStart = pos;
            while (type.charAt(--charsetStart) != ';' && charsetStart > 0) {
            }
            StringBuilder contentTypeBuilder = new StringBuilder();
            contentTypeBuilder.append(type.substring(0, charsetStart));
            if (i != type.length()) {
               contentTypeBuilder.append(type.substring(i));
            }
            contentType = contentTypeBuilder.toString();
         }
         //strip any trailing semicolon
         for (int i = contentType.length() - 1; i >= 0; --i) {
            char c = contentType.charAt(i);
            if (c == ' ' || c == '\t') {
               continue;
            }
            if (c == ';') {
               contentType = contentType.substring(0, i);
            }
            break;
         }
      }
      ContentTypeInfo ct = null;
      if(charset == null) {
         ct = new ContentTypeInfo(contentType, null, contentType);
      } else {
         ct = new ContentTypeInfo(contentType + ";charset=" + charset, charset,  contentType);
      }
      return ct;
   }

   /**
    * Converts a date to a format suitable for use in a HTTP request
    *
    * @param date The date
    * @return The RFC-1123 formatted date
    * @author Stuart Douglas
    */
   public static String toDateString(final Date date) {
      //we always need to set the time zone
      //because date format is stupid, and calling parse() can mutate the timezone
      //see UNDERTOW-458
      SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
      return SDF.format(date);
   }

   /**
    * @author Stuart Douglas
    */
   private class ContentTypeInfo {
      private final String header;
      private final String charset;
      private final String contentType;

      ContentTypeInfo(final String header, final String charset, final String contentType) {
         this.header = header;
         this.charset = charset;
         this.contentType = contentType;
      }

      public String getHeader() {
         return header;
      }

      public String getCharset() {
         return charset;
      }

      public String getContentType() {
         return contentType;
      }
   }
}
