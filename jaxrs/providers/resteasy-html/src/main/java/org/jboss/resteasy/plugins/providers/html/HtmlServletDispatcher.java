package org.jboss.resteasy.plugins.providers.html;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.spi.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class HtmlServletDispatcher extends HttpServletDispatcher
{

   private static final long serialVersionUID = 3793362217679129985L;

   @Override
   protected HttpResponse createServletResponse(HttpServletResponse response)
   {
      return new HttpServletResponseWrapper(response, getDispatcher().getProviderFactory())
      {
         
         protected OutputStream getSuperOuptutStream() throws IOException{
            return super.getOutputStream();
         }
         
         public OutputStream getOutputStream() throws IOException
         {
            return new OutputStream()
            {
               @Override
               public void write(int b) throws IOException
               {
                  getSuperOuptutStream().write(b);
               }

               @Override
               public void write(byte[] b) throws IOException
               {
                  getSuperOuptutStream().write(b);
               }

               @Override
               public void write(byte[] b, int off, int len) throws IOException
               {
                  getSuperOuptutStream().write(b, off, len);
               }

               @Override
               public void flush() throws IOException
               {
                  getSuperOuptutStream().flush();
               }

               @Override
               public void close() throws IOException
               {
                  getSuperOuptutStream().close();
               }
               
            };
         }
      };
   }
}
