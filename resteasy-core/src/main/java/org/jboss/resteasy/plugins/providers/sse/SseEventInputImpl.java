package org.jboss.resteasy.plugins.providers.sse;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.sse.InboundSseEvent;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
//import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.MediaTypeHelper;

public class SseEventInputImpl implements EventInput, Closeable
{
   private Annotation[] annotations;

   private MediaType mediaType;

   private boolean textLike;

   private MultivaluedMap<String, String> httpHeaders;

   private InputStream inputStream;

   private volatile boolean isClosed = false;

   private boolean lastFieldWasData;

   private boolean escape = false;

   private Providers providers;


   public SseEventInputImpl(final Annotation[] annotations, final MediaType streamType, final MediaType elementType,
                            final MultivaluedMap<String, String> httpHeaders, final InputStream inputStream)
   {
      this.annotations = annotations;
      this.mediaType = elementType;
      this.httpHeaders = httpHeaders;
      this.inputStream = inputStream;
      this.textLike = MediaTypeHelper.isTextLike(streamType);
      this.escape = streamType != null && streamType.toString().startsWith("application/x-stream-general");
   }

   @Override
   public void close() throws IOException
   {
      isClosed = true;
      this.inputStream.close();
   }

   public boolean isClosed()
   {
      return isClosed;
   }

   public InboundSseEvent read() throws IOException
   {
      Charset charset = StandardCharsets.UTF_8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }
      if (!textLike && escape) {
         charset = Charset.forName("ISO-8859-1");
      }
      @SuppressWarnings("resource")
      java.io.BufferedReader bufferReader = new java.io.BufferedReader(new InputStreamReader(inputStream, charset));
      lastFieldWasData = false;
      String line = bufferReader.readLine();
      try
      {
         if (line == null)
         {
            close();
            return null;
         }
      }
      catch (IOException e1)
      {
         try
         {
            close();
         }
         catch (IOException e)
         {
            //TODO: add a log message
         }
         throw e1;
      }
      boolean fieldSet = false;
      final InboundSseEventImpl.Builder eventBuilder = new InboundSseEventImpl.Builder(annotations, mediaType,
            httpHeaders);
      //TODO: Look at if this can be improved
      while (line != null)
      {
         if (line.isEmpty() && fieldSet)
         {
            InboundSseEventImpl event = (InboundSseEventImpl) eventBuilder.build();
            if (this.providers != null)
            {
               event.setProvider(this.providers);
            }
            return event;
         }
         if (!line.isEmpty())
         {
            int colon = line.indexOf(":");
            String filedValue = line.substring(colon + 2);
            if (colon == 0) {
               eventBuilder.commentLine(filedValue);
            }
            else
            {
               String fieldName = line.substring(0, colon);
               processField(eventBuilder, fieldName, mediaType, filedValue);
            }
            fieldSet = true;
         }
         line = bufferReader.readLine();
      }
      return null;

   }

   private void processField(final InboundSseEventImpl.Builder inboundEventBuilder, final String name,
         final MediaType mediaType, final String valueString)
   {
      boolean newLastFieldWasData = false;
      if ("event".equals(name))
      {
         inboundEventBuilder.name(valueString);
      }
      else if ("data".equals(name))
      {
         if (lastFieldWasData)
         {
            inboundEventBuilder.write(SseConstants.EOL);
         }
         if (!textLike && escape)
         {
            byte[] values = null;
            try
            {
               values = valueString.getBytes("ISO-8859-1");
            }
            catch (UnsupportedEncodingException e)
            {
               //
            }
            if (values == null)
            {
               return;
            }
            byte[] res = new byte[values.length];
            int b = 0;
            for (int i = 0; i < values.length; i++)
            {
               if (values[i] == '\\' && i + 1 < values.length && values[i + 1] == 'n')
               {
                  res[b] = (byte) ('\n');
                  i++;
               }
               else if (values[i] == '\\' && i + 1 < values.length && values[i + 1] == 'r')
               {
                  res[b] = (byte) ('\r');
                  i++;
               }
               else if (values[i] == '\\' && i + 1 < values.length && values[i + 1] == '\\')
               {
                  res[b] = (byte) ('\\');
                  i++;
               }
               else
               {
                  res[b] = values[i];
               }
               b++;
            }
            inboundEventBuilder.write(Arrays.copyOf(res, b));
         }
         else
         {
            inboundEventBuilder.write(valueString.getBytes());
         }
         newLastFieldWasData = true;
      }
      else if ("id".equals(name))
      {
         inboundEventBuilder.id(valueString);
      }
      else if ("retry".equals(name))
      {
         try
         {
            inboundEventBuilder.reconnectDelay(Long.parseLong(valueString));
         }
         catch (final NumberFormatException ex)
         {
            LogMessages.LOGGER.skipIllegalField("retry", valueString);
         }
      }
      else
      {
         LogMessages.LOGGER.skipUnkownFiled(name);
      }
      lastFieldWasData = newLastFieldWasData;
   }
   public void setProviders(Providers providers)
   {
      this.providers = providers;
   }
}
