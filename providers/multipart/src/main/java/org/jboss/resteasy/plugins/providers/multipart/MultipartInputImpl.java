package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartInputImpl implements MultipartInput
{
   protected String boundary;
   protected byte[] boundaryBytes;
   protected int pointer;
   protected List<InputPart> parts = new ArrayList<InputPart>();
   protected PartImpl currPart;
   protected int preambleEnd;
   protected Providers workers;
   protected static final Annotation[] empty = {};

   protected ByteArrayOutputStream baos = new ByteArrayOutputStream();
   protected byte[] buffer;

   public MultipartInputImpl(String boundary, Providers workers)
   {
      this.boundary = "--" + boundary;
      boundaryBytes = this.boundary.getBytes();
      this.workers = workers;
   }

   public List<InputPart> getParts()
   {
      return parts;
   }

   public class PartImpl implements InputPart
   {
      private int start;
      private int end;
      private MultivaluedMap<String, String> headers = new CaseInsensitiveMap<String>();
      private MediaType mediaType;

      public MultivaluedMap<String, String> getHeaders()
      {
         return headers;
      }

      public void startBody(int index)
      {
         start = index;
      }

      public void endBody(int index)
      {
         end = index;
         String mime = headers.getFirst("content-type");
         if (mime == null) mediaType = MediaType.TEXT_PLAIN_TYPE;
         mediaType = MediaType.valueOf(mime);
      }

      public void addHeader(String header)
      {
         int colon = header.indexOf(':');
         String name = header.substring(0, colon);
         String value = header.substring(colon + 1);
         if (value.charAt(0) == '"') value = value.substring(1);
         if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);
         headers.add(name.trim(), value.trim());
      }

      public InputStream getBody()
      {
         return new ByteArrayInputStream(buffer, start, end - start);
      }

      public String getBodyAsString()
      {
         return new String(buffer, start, end - start);
      }

      public <T> T getBody(Class<T> type, Type genericType) throws IOException
      {
         MessageBodyReader<T> reader = workers.getMessageBodyReader(type, genericType, empty, mediaType);
         return reader.readFrom(type, genericType, empty, mediaType, headers, getBody());
      }

      public <T> T getBody(GenericType<T> type) throws IOException
      {
         return getBody(type.getType(), type.getGenericType());
      }

      public MediaType getMediaType()
      {
         return mediaType;
      }
   }

   protected PartImpl createPart()
   {
      return new PartImpl();
   }

   public void parse(InputStream is) throws IOException
   {
      int index = 0;
      while (true)
      {
         int b = read(is);
         if (b == boundaryBytes[index])
         {
            index++;
            if (index == boundaryBytes.length)
            {
               int b1 = read(is);
               if (b1 == -1) throw new RuntimeException("Unexpected end of request, read bounder then EOF");
               int b2 = read(is);
               if (b2 == -1) throw new RuntimeException("Unexpected end of request, read bounder then EOF");

               if (b1 == '\r' && b2 == '\n')
               {
                  if (currPart != null) currPart.endBody(pointer - 4 - boundaryBytes.length);
                  else
                  {
                     preambleEnd = pointer - 4 - boundaryBytes.length;
                  }
                  currPart = createPart();
                  extractPart(is);
               }
               else if (b1 == '-' && b2 == '-')
               {
                  if (currPart != null) currPart.endBody(pointer - 4 - boundaryBytes.length);
                  else
                  {
                     preambleEnd = pointer - 4 - boundaryBytes.length;
                  }
                  break;
               }
               else
               {
                  throw new RuntimeException("Found boundary but no trailing \\r\\n or --");
               }
               index = 0;

            }
         }
         else
         {
            index = 0;
         }
      }
      buffer = baos.toByteArray();
   }

   protected void extractPart(InputStream is)
           throws IOException
   {
      parts.add(currPart);
      String line = null;
      do
      {
         line = readLine(is);
         if (!"".equals(line))
         {
            currPart.addHeader(line);
         }

      } while (line.length() > 0);
      currPart.startBody(pointer);
   }

   public String getPreamble()
   {
      if (preambleEnd < 0) return null;
      return new String(buffer, 0, preambleEnd);
   }

   public String getBufferAsString()
   {
      return new String(buffer);
   }

   protected String readLine(InputStream is) throws IOException
   {
      StringBuffer buf = new StringBuffer();
      while (true)
      {
         int b = read(is);
         if (b == -1) throw new RuntimeException("Unexpected end of buffer");
         if (b == '\r')
         {
            b = read(is);
            if (b == '\n') return buf.toString();
            else
            {
               buf.append('\r').append((char) b);
            }
         }
         else
         {
            buf.append((char) b);
         }

      }
   }

   protected int read(InputStream is)
           throws IOException
   {
      int b = is.read();
      if (b == -1) return -1;
      baos.write(b);
      pointer++;
      return b;
   }

   public static void main(String[] args) throws Exception
   {
      String input = "URLSTR: file:/Users/billburke/jboss/resteasy-jaxrs/resteasy-jaxrs/src/test/test-data/data.txt\r\n" +
              "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n" +
              "Content-Disposition: form-data; name=\"part1\"\r\n" +
              "Content-Type: text/plain; charset=US-ASCII\r\n" +
              "Content-Transfer-Encoding: 8bit\r\n" +
              "\r\n" +
              "This is Value 1\r\n" +
              "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n" +
              "Content-Disposition: form-data; name=\"part2\"\r\n" +
              "Content-Type: text/plain; charset=US-ASCII\r\n" +
              "Content-Transfer-Encoding: 8bit\r\n" +
              "\r\n" +
              "This is Value 2\r\n" +
              "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n" +
              "Content-Disposition: form-data; name=\"data.txt\"; filename=\"data.txt\"\r\n" +
              "Content-Type: application/octet-stream; charset=ISO-8859-1\r\n" +
              "Content-Transfer-Encoding: binary\r\n" +
              "\r\n" +
              "hello world\r\n" +
              "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3--";
      ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
      MultipartInputImpl multipart = new MultipartInputImpl("B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3", null);
      multipart.parse(bais);

      System.out.println(multipart.getPreamble());
      System.out.println("**********");
      for (InputPart part : multipart.getParts())
      {
         System.out.println("--");
         System.out.println("\"" + part.getBodyAsString() + "\"");
      }
      System.out.println("done");

   }
}
