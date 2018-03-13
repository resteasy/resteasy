package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.core.ServerResponseWriter;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class SseEventOutputImpl extends GenericType<OutboundSseEvent> implements SseEventSink
{
   private final MessageBodyWriter<OutboundSseEvent> writer;

   private final ResteasyAsynchronousContext asyncContext;

   private final HttpResponse response;

   private final HttpRequest request;

   private volatile boolean closed;

   private final Map<Class<?>, Object> contextDataMap;

   private boolean responseFlushed = false;
   
   private final Object lock = new Object();

   public SseEventOutputImpl(final MessageBodyWriter<OutboundSseEvent> writer)
   {
      this.writer = writer;
      contextDataMap = ResteasyProviderFactory.getContextDataMap();

      request = ResteasyProviderFactory.getContextData(org.jboss.resteasy.spi.HttpRequest.class);
      asyncContext = request.getAsyncContext();

      if (!asyncContext.isSuspended())
      {
         try
         {
            asyncContext.suspend();
         }
         catch (IllegalStateException ex)
         {
            LogMessages.LOGGER.failedToSetRequestAsync();
         }
      }

      response = ResteasyProviderFactory.getContextData(HttpResponse.class);
   }

   @Override
   public void close()
   {
      synchronized (lock)
      {
         closed = true;
         if (asyncContext.isSuspended())
         {
            ResteasyAsynchronousResponse asyncResponse = asyncContext.getAsyncResponse();
            if (asyncResponse != null)
            {
               asyncResponse.complete();
            }
         }
      }
   }
   
   protected void flushResponseToClient()
   {
      try
      {
         internalFlushResponseToClient(false);
      }
      catch (IOException e)
      {
      }
   }
   
   private void internalFlushResponseToClient(boolean throwIOException) throws IOException
   {
      synchronized (lock)
      {
         if (!responseFlushed)
         {
            BuiltResponse jaxrsResponse = null;
            if (this.closed)
            {
               jaxrsResponse = (BuiltResponse) Response.noContent().build();
            }
            else
            {
               //set back to client 200 OK to implies the SseEventOutput is ready
               jaxrsResponse = (BuiltResponse) Response.ok().type(MediaType.SERVER_SENT_EVENTS).build();
            }

            try
            {
               ServerResponseWriter.writeNomapResponse(jaxrsResponse, request, response,
                     ResteasyProviderFactory.getInstance(), t -> {
                     }, true);
               response.getOutputStream().write(SseConstants.EOL);
               response.getOutputStream().write(SseConstants.EOL);
               response.flushBuffer();
               responseFlushed = true;
            }
            catch (IOException e)
            {
               close();
               if (throwIOException)
               {
                  throw e;
               }
               throw new ProcessingException(Messages.MESSAGES.failedToCreateSseEventOutput(), e);
            }
         }
      }
   }

   @Override
   public boolean isClosed()
   {
      return closed;
   }

   @Override
   public CompletionStage<?> send(OutboundSseEvent event)
   {
      synchronized (lock)
      {
         if (closed)
         {
            throw new IllegalStateException(Messages.MESSAGES.sseEventSinkIsClosed());
         }
         try
         {
            internalFlushResponseToClient(true);
            writeEvent(event);

         }
         catch (Exception ex)
         {
            CompletableFuture<Void> completableFuture = new CompletableFuture<>();
            completableFuture.completeExceptionally(ex);
            return completableFuture;
         }
         return CompletableFuture.completedFuture(event);
      }
   }

   protected void writeEvent(OutboundSseEvent event) throws IOException
   {
      synchronized (lock)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         try
         {
            if (event != null)
            {
               ByteArrayOutputStream bout = new ByteArrayOutputStream();
               writer.writeTo(event, event.getClass(), null, new Annotation[]
               {}, event.getMediaType(), null, bout);
               response.getOutputStream().write(bout.toByteArray());
               response.flushBuffer();
            }
         }
         catch (IOException e)
         {
            //The connection could be broken or closed. whenever IO error happens, mark closed to true to 
            //stop event writing 
            close();
            LogMessages.LOGGER.failedToWriteSseEvent(event.toString(), e);
            throw e;
         }
         catch (Exception e)
         {
            LogMessages.LOGGER.failedToWriteSseEvent(event.toString(), e);
            throw new ProcessingException(e);
         }
         finally
         {
            ResteasyProviderFactory.removeContextDataLevel();
         }
      }
   }

}
