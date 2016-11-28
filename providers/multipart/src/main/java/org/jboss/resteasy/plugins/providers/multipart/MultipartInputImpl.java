package org.jboss.resteasy.plugins.providers.multipart;

import java.nio.charset.Charset;
import java.util.HashMap;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.codec.Base64InputStream;
import org.apache.james.mime4j.codec.QuotedPrintableInputStream;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.apache.james.mime4j.field.ContentTypeField;
import org.apache.james.mime4j.field.DefaultFieldParser;
import org.apache.james.mime4j.field.ParsedField;
import org.apache.james.mime4j.message.*;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.storage.DefaultStorageProvider;
import org.apache.james.mime4j.storage.StorageProvider;
import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.ContentUtil;
import org.apache.james.mime4j.util.MimeUtil;
import org.jboss.resteasy.core.ProvidersContextRetainer;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.GenericType;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartInputImpl implements MultipartInput, ProvidersContextRetainer
{
    private static final org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(MultipartInputImpl.class);

   protected MediaType contentType;
   protected Providers workers;
   protected Message mimeMessage;
   protected List<InputPart> parts = new ArrayList<InputPart>();
   protected static final Annotation[] empty = {};
   protected MediaType defaultPartContentType = MultipartConstants.TEXT_PLAIN_WITH_CHARSET_US_ASCII_TYPE;
   protected String defaultPartCharset = null;
   protected Providers savedProviders;

   // We hack MIME4j so that it always returns a BinaryBody so we don't have to deal with Readers and their charset conversions
   private static class BinaryOnlyMessageBuilder extends MessageBuilder
   {
      private Method expectMethod;
      private java.lang.reflect.Field bodyFactoryField;
      private java.lang.reflect.Field stackField;
      private Charset charset;

      private void init()
      {
         try
         {
            expectMethod = MessageBuilder.class.getDeclaredMethod("expect", Class.class);
            expectMethod.setAccessible(true);
            bodyFactoryField = MessageBuilder.class.getDeclaredField("bodyFactory");
            bodyFactoryField.setAccessible(true);
            stackField = MessageBuilder.class.getDeclaredField("stack");
            stackField.setAccessible(true);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }

       public void initCharset(String charset)
       {
           try
           {
               logger.info("Set charset init " + charset);
               if(charset != null){
                   this.charset = Charset.forName(charset);
               }
           }
           catch (Exception e)
           {
               // nothing to do
           }
       }

      private BinaryOnlyMessageBuilder(Entity entity)
      {
         super(entity);
         init();
      }

      private BinaryOnlyMessageBuilder(Entity entity, StorageProvider storageProvider)
      {
         super(entity, storageProvider);
         init();
      }

      @Override
      public void field(Field field) throws MimeException {
         Charset charsetField = getCharset();
         if(charsetField != null){
             this.expected(Header.class);
             ParsedField parsedField = BinaryAbstractField.parse(charsetField, field.getRaw());
             ((Header) getStack().peek()).addField(parsedField);
         } else {
            super.field(field);
         }
      }

      @Override
      public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
         expected(Entity.class);

         final String enc = bd.getTransferEncoding();
         final Body body;

         final InputStream decodedStream;
         if (MimeUtil.ENC_BASE64.equals(enc)) {
            decodedStream = new Base64InputStream(is);
         } else if (MimeUtil.ENC_QUOTED_PRINTABLE.equals(enc)) {
            decodedStream = new QuotedPrintableInputStream(is);
         } else {
            decodedStream = is;
         }

         try {
            BodyFactory factory = getBodyFactory();
            body = factory.binaryBody(decodedStream);

            Stack<Object> st = getStack();
            Entity entity = ((Entity) st.peek());
            entity.setBody(body);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }

      }

      private Charset getCharset(){
         return charset;
      }

      private BodyFactory getBodyFactory(){
         try {
            return (BodyFactory) bodyFactoryField.get(this);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      private Stack<Object> getStack(){
         try {
            return (Stack<Object>) stackField.get(this);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      private void expected(Class<?> c){
         try {
            expectMethod.invoke(this, c);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   private static class BinaryMessage extends Message
   {
      private BinaryMessage(InputStream is, String charset) throws IOException, MimeIOException
      {
         try {
             BinaryOnlyMessageBuilder bomb = new BinaryOnlyMessageBuilder(this, DefaultStorageProvider.getInstance());
             bomb.initCharset(charset);
             MimeStreamParser parser = new MimeStreamParser(null);
             parser.setContentHandler(bomb);
             parser.parse(is);
         } catch (MimeException e) {
            throw new MimeIOException(e);
         }

      }

       private BinaryMessage(InputStream is) throws IOException, MimeIOException
       {
           try {
               BinaryOnlyMessageBuilder bomb = new BinaryOnlyMessageBuilder(this, DefaultStorageProvider.getInstance());
               MimeStreamParser parser = new MimeStreamParser(null);
               parser.setContentHandler(bomb);
               parser.parse(is);
           } catch (MimeException e) {
               throw new MimeIOException(e);
           }

       }
   }

   private static class BinaryAbstractField {

      private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("^([\\x21-\\x39\\x3b-\\x7e]+):");
      private static final DefaultFieldParser DEFAULT_FIELD_PARSER = new DefaultFieldParser();

      public static ParsedField parse(ByteSequence raw) throws MimeException {
         String rawStr = ContentUtil.decode(raw);
         return parse(raw, rawStr);
      }

      public static ParsedField parse(final Charset charset, final ByteSequence raw) throws MimeException {
         final String rawStr = ContentUtil.decode(charset, raw);
         return parse(raw, rawStr);
      }

      private static ParsedField parse(ByteSequence raw, String rawStr) throws MimeException {
         final String unfolded = MimeUtil.unfold(rawStr);
         final Matcher fieldMatcher = FIELD_NAME_PATTERN.matcher(unfolded);
         if (!fieldMatcher.find()) {
            throw new MimeException("Invalid field in string");
         }
         final String name = fieldMatcher.group(1);
         String body = unfolded.substring(fieldMatcher.end());
         if (body.length() > 0 && body.charAt(0) == 32) {
            body = body.substring(1);
         }
         return DEFAULT_FIELD_PARSER.parse(name, body, raw);

      }
   }

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
   
   public MultipartInputImpl(Multipart multipart, Providers workers) throws IOException
   {
      for (BodyPart bodyPart : multipart.getBodyParts())
         parts.add(extractPart(bodyPart));
      this.workers = workers;
   }

   public void parse(InputStream is) throws IOException
   {
      if(defaultPartCharset != null){
          mimeMessage = new BinaryMessage(addHeaderToHeadlessStream(is), defaultPartCharset);
      } else {
          mimeMessage = new BinaryMessage(addHeaderToHeadlessStream(is));
      }
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
      return new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8));
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

      @SuppressWarnings("unchecked")
      public <T> T getBody(Class<T> type, Type genericType)
              throws IOException
      {
         if (MultipartInput.class.equals(type))
         {
            if (bodyPart.getBody() instanceof Multipart)
            {
               return (T) new MultipartInputImpl(Multipart.class.cast(bodyPart.getBody()), workers);
            }
         }
         try
         {
            if (savedProviders != null)
            {
               ResteasyProviderFactory.pushContext(Providers.class, savedProviders);  
            }
            MessageBodyReader<T> reader = workers.getMessageBodyReader(type, genericType, empty, contentType);
            if (reader == null)
            {
               throw new RuntimeException(Messages.MESSAGES.unableToFindMessageBodyReader(contentType, type.getName()));
            }

            return reader.readFrom(type, genericType, empty, contentType, headers, getBody());
         }
         finally
         {
            if (savedProviders != null)
            {
               ResteasyProviderFactory.popContextData(Providers.class);
            }
         }
      }

      @SuppressWarnings("unchecked")
      public <T> T getBody(GenericType<T> type) throws IOException
      {
         return getBody((Class<T>) type.getRawType(), type.getType());
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
      Map<String, String> parameters = new LinkedHashMap<String, String>();
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
      Map<String, String> newParams = new LinkedHashMap<String, String>();
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

   @Override
   public void setProviders(Providers providers)
   {
      savedProviders = providers;
   }

}
