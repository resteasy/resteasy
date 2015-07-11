package org.jboss.resteasy.plugins.providers.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.WebApplicationException;

public class Viewable extends View
{

   public Viewable(String path)
   {
      super(path);
   }

   public Viewable(String path, Object model)
   {
      super(path, model);
   }

   public Viewable(String path, Object model, String modelName)
   {
      super(path, model, modelName);
   }

   @Override
   public void render(HttpServletRequest request, HttpServletResponse response, OutputStream entityStream)
      throws IOException, ServletException, WebApplicationException
   {
      ResponseWrapper responseWrapper = new ResponseWrapper(response, entityStream);

      addModelTo(request);

      getRequestDispatcherFrom(request).include(request, responseWrapper);
      responseWrapper.getWriter().flush();
   }

   private static class ResponseWrapper extends HttpServletResponseWrapper
   {

      private final PrintWriter pw;
      private final ServletOutputStream sos;

      public ResponseWrapper(HttpServletResponse response, OutputStream os)
      {
         super(response);

         pw = new PrintWriter(os);
         sos = new ServletOutputStreamWrapper(os);
      }

      @Override
      public PrintWriter getWriter()
      {
         return pw;
      }

      @Override
      public ServletOutputStream getOutputStream() throws IOException
      {
         return sos;
      }
   }

   public static class ServletOutputStreamWrapper extends ServletOutputStream
   {

      private final OutputStream os;

      public ServletOutputStreamWrapper(OutputStream os)
      {
         this.os = os;
      }

      @Override
      public void write(int b) throws IOException
      {
         os.write(b);
      }

      @Override
      public boolean isReady()
      {
         return true;
      }

      @Override
      public void setWriteListener(WriteListener wl)
      {
      }
   }
}
