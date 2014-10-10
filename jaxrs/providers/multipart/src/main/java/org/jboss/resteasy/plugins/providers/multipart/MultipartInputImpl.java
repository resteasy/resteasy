package org.jboss.resteasy.plugins.providers.multipart;

import org.apache.james.mime4j.dom.field.ContentTypeField;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.stream.Field;
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
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.james.mime4j.dom.Entity;

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
   protected String defaultPartCharset = null;

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
         this.defaultPartCharset = (String) httpRequest.getAttribute(InputPart.DEFAULT_CHARSET_PROPERTY);
         if (defaultPartCharset != null)
         {
            this.defaultPartContentType = getMediaTypeWithDefaultCharset(this.defaultPartContentType);
         }
      }
   }

   public MultipartInputImpl(MediaType contentType, Providers workers,
                             MediaType defaultPartContentType, String defaultPartCharset)
   {
      this.contentType = contentType;
      this.workers = workers;
      if (defaultPartContentType != null) this.defaultPartContentType = defaultPartContentType;
      this.defaultPartCharset = defaultPartCharset;
      if (defaultPartCharset != null)
      {
         this.defaultPartContentType = getMediaTypeWithDefaultCharset(this.defaultPartContentType);
      }
   }

   public void parse(InputStream is) throws IOException
   {
      mimeMessage = Mime4JWorkaround.parseMessage(addHeaderToHeadlessStream(is));
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
      for (Entity entity : multipart.getBodyParts())
          if (entity instanceof BodyPart)
          {
            parts.add(extractPart((BodyPart)entity));
          }
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
         if (getCharset(contentType) == null)
         {
            if (defaultPartCharset != null)
            {
               contentType = getMediaTypeWithDefaultCharset(contentType);
            }
            else if (contentType.getType().equalsIgnoreCase("text"))
            {
               contentType = getMediaTypeWithCharset(contentType, "us-ascii");
            }
         }
      }

      @Override
      public void setMediaType(MediaType mediaType)
      {
         contentType = mediaType;
         contentTypeFromMessage = false;
         headers.putSingle("Content-Type", mediaType.toString());
      }

      public <T> T getBody(Class<T> type, Type genericType)
              throws IOException
      {
         MessageBodyReader<T> reader = workers.getMessageBodyReader(type,
                 genericType, empty, contentType);
         if (reader == null)
         {
            throw new RuntimeException("Unable to find a MessageBodyReader for media type: " + contentType + " and class type " + type.getName());
         }

         return reader.readFrom(type, genericType, empty, contentType, headers, getBody());

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
            throw new UnsupportedOperationException();
            /*
            InputStreamReader reader = (InputStreamReader)((TextBody) body).getReader();
            StringBuilder inputBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            while (true) {
               int readCount = reader.read(buffer);
               if (readCount < 0) {
                  break;
               }
               inputBuilder.append(buffer, 0, readCount);
            }
            String str = inputBuilder.toString();
            return new ByteArrayInputStream(str.getBytes(reader.getEncoding()));
            */
         }
         else if (body instanceof BinaryBody)
         {
            return ((BinaryBody)body).getInputStream();
         }
         return result;
      }

      public String getBodyAsString() throws IOException
      {
         return getBody(String.class, null);
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

   @Override
   public void close()
   {
      if (mimeMessage != null)
      {
         try
         {
            mimeMessage.dispose();
         }
         catch (Exception e)
         {

         }
      }
   }

   protected void finalize() throws Throwable
   {
      close();
   }

   protected String getCharset(MediaType mediaType)
   {
      for (Iterator<String> it = mediaType.getParameters().keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();
         if ("charset".equalsIgnoreCase(key))
         {
            return mediaType.getParameters().get(key);
         }
      }
      return null;
   }
   
   private MediaType getMediaTypeWithDefaultCharset(MediaType mediaType)
   {
      String charset = defaultPartCharset;
      return getMediaTypeWithCharset(mediaType, charset);
   }

   private MediaType getMediaTypeWithCharset(MediaType mediaType, String charset)
   {
      Map<String, String> params = mediaType.getParameters();
      Map<String, String> newParams = new HashMap<String, String>();
      newParams.put("charset", charset);
      for (Iterator<String> it = params.keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();
         if (!"charset".equalsIgnoreCase(key))
         {
            newParams.put(key, params.get(key));
         }
      }
      return new MediaType(mediaType.getType(), mediaType.getSubtype(), newParams);
   }

}
