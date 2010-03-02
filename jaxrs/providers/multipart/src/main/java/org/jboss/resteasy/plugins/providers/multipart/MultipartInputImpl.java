package org.jboss.resteasy.plugins.providers.multipart;

import org.apache.james.mime4j.field.ContentTypeField;
import org.apache.james.mime4j.message.BinaryBody;
import org.apache.james.mime4j.message.Body;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.apache.james.mime4j.message.TextBody;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.CharsetUtil;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartInputImpl implements MultipartInput
{
   protected MediaType contentType;
   protected Providers workers;
   protected Message mimeMessage;
   protected List<InputPart> parts = new ArrayList<InputPart>();
   protected static final Annotation[] empty = {};
   protected MediaType defaultPartContentType = MultipartConstants.TEXT_PLAIN_WITH_CHARSET_US_ASCII_TYPE;

   public MultipartInputImpl(MediaType contentType, Providers workers)
   {
      this.contentType = contentType;
      this.workers = workers;
      HttpRequest httpRequest = ResteasyProviderFactory
              .getContextData(HttpRequest.class);
      if (httpRequest != null)
      {
         String defaultContentType = (String) httpRequest
                 .getAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY);
         if (defaultContentType != null)
            this.defaultPartContentType = MediaType
                    .valueOf(defaultContentType);
      }
   }

   public MultipartInputImpl(MediaType contentType, Providers workers,
                             MediaType defaultPartContentType)
   {
      this.contentType = contentType;
      this.workers = workers;
      this.defaultPartContentType = defaultPartContentType;
   }

   public void parse(InputStream is) throws IOException
   {
      mimeMessage = new Message(addHeaderToHeadlessStream(is));
      extractParts();
   }

   protected InputStream addHeaderToHeadlessStream(InputStream is)
           throws UnsupportedEncodingException
   {
      return new SequenceInputStream(createHeaderInputStream(), is);
   }

   protected InputStream createHeaderInputStream()
           throws UnsupportedEncodingException
   {
      String header = HttpHeaders.CONTENT_TYPE + ": " + contentType
              + "\r\n\r\n";
      return new ByteArrayInputStream(header.getBytes("utf-8"));
   }

   public String getPreamble()
   {
      return ((Multipart) mimeMessage.getBody()).getPreamble();
   }

   public List<InputPart> getParts()
   {
      return parts;
   }

   protected void extractParts() throws IOException
   {
      Multipart multipart = (Multipart) mimeMessage.getBody();
      for (BodyPart bodyPart : multipart.getBodyParts())
         parts.add(extractPart(bodyPart));
   }

   protected InputPart extractPart(BodyPart bodyPart) throws IOException
   {
      return new PartImpl(bodyPart);
   }

   public class PartImpl implements InputPart
   {

      private BodyPart bodyPart;
      private MediaType contentType;
      private MultivaluedMap<String, String> headers = new CaseInsensitiveMap<String>();
      private boolean contentTypeFromMessage;

      public PartImpl(BodyPart bodyPart)
      {
         this.bodyPart = bodyPart;
         for (Field field : bodyPart.getHeader())
         {
            headers.add(field.getName(), field.getBody());
            if (field instanceof ContentTypeField)
            {
               contentType = MediaType.valueOf(field.getBody());
               contentTypeFromMessage = true;
            }
         }
         if (contentType == null)
            contentType = defaultPartContentType;
      }

      public <T> T getBody(Class<T> type, Type genericType)
              throws IOException
      {
         MessageBodyReader<T> reader = workers.getMessageBodyReader(type,
                 genericType, empty, contentType);
         return reader.readFrom(type, genericType, empty, contentType,
                 headers, getBody());
      }

      public <T> T getBody(GenericType<T> type) throws IOException
      {
         return getBody(type.getType(), type.getGenericType());
      }

      public InputStream getBody() throws IOException
      {
         Body body = bodyPart.getBody();
         InputStream result = null;
         if (body instanceof TextBody)
         {
            Reader reader = ((TextBody) body).getReader();
            result = new ReaderBackedInputStream(reader);
         }
         else if (body instanceof BinaryBody)
            result = ((BinaryBody) body).getInputStream();
         return result;
      }

      public String getBodyAsString() throws IOException
      {
         Body body = bodyPart.getBody();
         String result = null;
         if (body instanceof TextBody)
         {
            Reader reader = ((TextBody) body).getReader();
            try
            {
               StringWriter writer = new StringWriter();
               char[] buffer = new char[4048];
               int n = 0;
               while ((n = reader.read(buffer)) != -1)
                  writer.write(buffer, 0, n);
               result = writer.toString();
            }
            finally
            {
               reader.close();
            }
         }
         else if (body instanceof BinaryBody)
         {
            InputStream inputStream = ((BinaryBody) body).getInputStream();
            InputStreamReader inputStreamReader = null;
            try
            {
               String charset = contentType.getParameters().get("charset");
               if (charset != null)
                  charset = CharsetUtil.toJavaCharset(charset);
               inputStreamReader = charset == null ? new InputStreamReader(
                       inputStream)
                       : new InputStreamReader(inputStream, charset);
               StringWriter writer = new StringWriter();
               char[] buffer = new char[4048];
               int n = 0;
               while ((n = inputStreamReader.read(buffer)) != -1)
                  writer.write(buffer, 0, n);
               result = writer.toString();
            }
            finally
            {
               if (inputStreamReader != null)
                  inputStreamReader.close();
               inputStream.close();
            }
         }

         return result;
      }

      public MultivaluedMap<String, String> getHeaders()
      {
         return headers;
      }

      public MediaType getMediaType()
      {
         return contentType;
      }

      public boolean isContentTypeFromMessage()
      {
         return contentTypeFromMessage;
      }
   }

   private static class ReaderBackedInputStream extends InputStream
   {
      private final Reader reader;

      private ReaderBackedInputStream(Reader reader)
      {
         this.reader = reader;
      }

      @Override
      public int read() throws IOException
      {
         int c = reader.read();
         return c;
      }

      @Override
      public void close() throws IOException
      {
         reader.close();
         super.close();
      }
   }

   public static void main(String[] args) throws Exception
   {
      String input = "URLSTR: file:/Users/billburke/jboss/resteasy-jaxrs/resteasy-jaxrs/src/test/test-data/data.txt\r\n"
              + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n"
              + "Content-Disposition: form-data; name=\"part1\"\r\n"
              + "Content-Type: text/plain; charset=US-ASCII\r\n"
              + "Content-Transfer-Encoding: 8bit\r\n"
              + "\r\n"
              + "This is Value 1\r\n"
              + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n"
              + "Content-Disposition: form-data; name=\"part2\"\r\n"
              + "Content-Type: text/plain; charset=US-ASCII\r\n"
              + "Content-Transfer-Encoding: 8bit\r\n"
              + "\r\n"
              + "This is Value 2\r\n"
              + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n"
              + "Content-Disposition: form-data; name=\"data.txt\"; filename=\"data.txt\"\r\n"
              + "Content-Type: application/octet-stream; charset=ISO-8859-1\r\n"
              + "Content-Transfer-Encoding: binary\r\n"
              + "\r\n"
              + "hello world\r\n" + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3--";
      ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("boundary", "B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3");
      MediaType contentType = new MediaType("multipart", "form-data",
              parameters);
      MultipartInputImpl multipart = new MultipartInputImpl(contentType, null);
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
