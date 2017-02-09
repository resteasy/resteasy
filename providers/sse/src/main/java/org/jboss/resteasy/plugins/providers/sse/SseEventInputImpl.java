package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventInput;

import org.jboss.resteasy.plugins.providers.sse.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.sse.i18n.Messages;

public class SseEventInputImpl implements SseEventInput
{
   private Annotation[] annotations;
   private MediaType mediaType;
   private MultivaluedMap<String, String> httpHeaders;
   private InputStream inputStream;
   private final byte[] EventEND = "\r\n\r\n".getBytes();
   private boolean isClosed = false;

   public SseEventInputImpl(Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
         InputStream inputStream)
   {
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.httpHeaders = httpHeaders;
      this.inputStream = inputStream;
   }

   @Override
   public void close() throws IOException
   {
      this.inputStream.close();
      isClosed = true;
   }

   @Override
   public boolean isClosed()
   {
      return isClosed;
   }

   @Override
   public InboundSseEvent read() throws IllegalStateException
   {
      byte[] chunk = null;
      try
      {
         chunk = readEvent(inputStream);
      }
      catch (IOException e1)
      {
         throw new RuntimeException(Messages.MESSAGES.readEventException(), e1);
      }
      if (chunk == null)
      {
         return null;
      }

      final ByteArrayInputStream entityStream = new ByteArrayInputStream(chunk);
      final ByteArrayOutputStream temSave = new ByteArrayOutputStream();
      Charset charset = SseConstants.UTF8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }
      final InboundSseEventImpl.Builder eventBuilder = new InboundSseEventImpl.Builder(annotations, mediaType,
            httpHeaders);
      int b = -1;
      SseConstants.EVENT currentState = SseConstants.EVENT.START;
      while ((b = entityStream.read()) != -1)
      {
         try
         {
            if (currentState == SseConstants.EVENT.START)
            {
               if (b == '\r' ||b == '\n')
               {
                  continue;
               }
               
               if (b == ':')
               {
                  currentState = SseConstants.EVENT.COMMENT;
                  continue;
               }
               else
               {
                  temSave.write(b);
                  currentState = SseConstants.EVENT.FIELD;
                  continue;
               }
            }
            if (currentState == SseConstants.EVENT.COMMENT)
            {

               b = readLine(entityStream, '\n', temSave);
               String commentLine = temSave.toString(charset.toString());               
               eventBuilder.commentLine(commentLine);
               temSave.reset();
               currentState = SseConstants.EVENT.START;
               continue;
            }
            if (currentState == SseConstants.EVENT.FIELD)
            {
               temSave.write(b);
               b = readLine(entityStream, ':', temSave);
               String fieldName = temSave.toString(charset.toString());
               temSave.reset();
               if (b == ':')
               {
                  //space after the colon is ignored
                  do
                  {
                     b = entityStream.read();
                  }
                  while (b == ' ');

                  if (b != '\n' && b != '\r' && b != -1)
                  {
                     temSave.write(b);
                     b = readLine(entityStream, '\n', temSave);
                  }
               }

               processField(eventBuilder, fieldName, mediaType, temSave.toByteArray());
               temSave.reset();
               currentState = SseConstants.EVENT.START;
               continue;
            }
         }
         catch (IOException e)
         {
            throw new RuntimeException(Messages.MESSAGES.readEventException(), e);
         }
      }
      return eventBuilder.build();
   }

   private int readLine(final InputStream in, final int delimiter, final OutputStream out) throws IOException
   {
      int b;
      while ((b = in.read()) != -1)
      {
         if (b == delimiter || b == '\n' || b == '\r')
         {
            break;
         }
         else if (out != null)
         {
            out.write(b);
         }
      }
      return b;
   }

   private void processField(final InboundSseEventImpl.Builder inboundEventBuilder, final String name,
         final MediaType mediaType, final byte[] value)
   {
      Charset charset = SseConstants.UTF8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }
      String valueString = new String(value, charset);
      if ("event".equals(name))
      {
         inboundEventBuilder.name(valueString);
      }
      else if ("data".equals(name))
      {
         inboundEventBuilder.write(value);
         inboundEventBuilder.write(SseConstants.EOL);
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
   }

   public byte[] readEvent(final InputStream in) throws IOException
   {
      @SuppressWarnings("resource")
      EventByteArrayOutputStream buffer = new EventByteArrayOutputStream();
      int data;
      int pos = 0;
      while ((data = in.read()) != -1)
      {
         byte b = (byte) data;
         if (b == EventEND[pos])
         {
            pos++;
         }
         else
         {
            pos = 0;
         }
         buffer.write(b);
         if (pos >= EventEND.length && buffer.toByteArray().length > EventEND.length)
         {
            return buffer.getEventPayLoad();
         }
         if (pos >= EventEND.length && buffer.toByteArray().length == EventEND.length)
         {
            pos = 0;
            buffer.reset();
            continue;
         }
      }
      return null;
   }
}
