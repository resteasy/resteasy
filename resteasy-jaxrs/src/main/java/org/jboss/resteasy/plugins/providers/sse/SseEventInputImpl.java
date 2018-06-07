package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.sse.InboundSseEvent;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
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

   private final String DELIMITER = new String(SseConstants.EVENT_DELIMITER, StandardCharsets.UTF_8);

   public SseEventInputImpl(Annotation[] annotations, MediaType streamType, MediaType elementType,
      MultivaluedMap<String, String> httpHeaders, InputStream inputStream)
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
      byte[] chunk = null;
      try
      {
         lastFieldWasData = false;
         chunk = readEvent(inputStream);
         if (chunk == null)
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

      final ByteArrayInputStream entityStream = new ByteArrayInputStream(chunk);
      final ByteArrayOutputStream temSave = new ByteArrayOutputStream();
      Charset charset = StandardCharsets.UTF_8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }

      final InboundSseEventImpl.Builder eventBuilder = new InboundSseEventImpl.Builder(annotations, mediaType,
            httpHeaders);
      //TODO: Look at if this can be improved
      int b = -1;
      SseConstants.EVENT currentState = SseConstants.EVENT.START;
      while ((b = entityStream.read()) != -1)
      {
         try
         {
            if (currentState == SseConstants.EVENT.START)
            {
               if (b == '\r' || b == '\n')
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
               String commentLine = temSave.toString(charset.name());
               eventBuilder.commentLine(commentLine);
               temSave.reset();
               currentState = SseConstants.EVENT.START;
               continue;
            }
            if (currentState == SseConstants.EVENT.FIELD)
            {
               temSave.write(b);
               b = readLine(entityStream, ':', temSave);
               String fieldName = temSave.toString(StandardCharsets.UTF_8.name());
               temSave.reset();
               if (b == ':')
               {
                  //spec says there is space after colon
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
            throw new IOException(Messages.MESSAGES.readEventException(), e);
         }
      }
      return eventBuilder.build();
   }

   private int readLine(final InputStream in, final int delimiter, final OutputStream out) throws IOException
   {
      int b;
      while ((b = in.read()) != -1)
      {
         if (!textLike && escape && b == '\\')
         {
             b = in.read();
             if (b != '\\' && b != '\n' && b != '\r')
             {
                 throw new RuntimeException(Messages.MESSAGES.expectedExcapedCharacter(b));
             }
             out.write(b);
             continue;
         }
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
      Charset charset = StandardCharsets.UTF_8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }
      String valueString = new String(value, charset);
      boolean newLastFieldWasData = false;
      if ("event".equals(name))
      {
         inboundEventBuilder.name(valueString);
      }
      else if ("data".equals(name))
      {
         if (lastFieldWasData)
            inboundEventBuilder.write(SseConstants.EOL);
         inboundEventBuilder.write(value);
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

   public byte[] readEvent(final InputStream in) throws IOException
   {
      @SuppressWarnings("resource")
      EventByteArrayOutputStream buffer = new EventByteArrayOutputStream();
      int data;
      int pos = 0;
      boolean boundary = false;
      byte[] eolBuffer = new byte[5];
      while ((data = in.read()) != -1)
      {
         byte b = (byte) data;
         if (!textLike && b == '\\')
         {
            buffer.write(b);
            b = (byte) in.read();
         }
         else
         {
            if (b == '\r' || b == '\n')
            {
                eolBuffer[pos] = b;
                //if it meets \r\r , \n\n , \r\n\r\n or \n\r\n\r\n
                if ((pos > 0 && eolBuffer[pos] == eolBuffer[pos - 1])
                    || (pos >= 3 && new String(eolBuffer, 0, pos, StandardCharsets.UTF_8).contains(DELIMITER)))
                {
                    boundary = true;
                }
                //take it a boundary if there are 5 unexpected eols  
                if (pos++ > 4)
                {
                    boundary = true;
                }
            }
            else
            {
                pos = 0;
            }
         }
         buffer.write(b);
         if (boundary && buffer.size() > pos)
         {
            return buffer.getEventPayLoad();
         }
         //if it's emtpy 
         if (boundary && buffer.size() == pos)
         {
            pos = 0;
            boundary = false;
            buffer.reset();
            continue;
         }
      }
      return null;
   }
}
