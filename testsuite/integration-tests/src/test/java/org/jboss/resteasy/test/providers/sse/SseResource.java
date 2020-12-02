package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.sse.SseConstants;

@Path("/server-sent-events")
public class SseResource
{

   public static final String jsonMessage = "{\n"
                                           + " \"message\": \"json\",\n"
                                           + " \"foo\": \"bar\"\n"
                                           + "}";
   private final Object outputLock = new Object();
   private final Object sseBroadcasterLock = new Object();

   @Context
   private Sse sse;

   @Context
   private ServletContext servletContext;

   private volatile SseEventSink eventSink;

   private volatile SseBroadcaster sseBroadcaster;

   private Object openLock = new Object();

   private volatile boolean sending = true;

   private List<OutboundSseEvent> eventsStore = new ArrayList<OutboundSseEvent>();

   private AtomicInteger noContentCount = new AtomicInteger();

   private static final Logger logger = Logger.getLogger(SseResource.class);

   @GET
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void getMessageQueue(@HeaderParam(SseConstants.LAST_EVENT_ID_HEADER) @DefaultValue("-1") int lastEventId,
         @Context SseEventSink eventSink)
   {
      synchronized (outputLock)
      {
         if (eventSink.isClosed())
         {
            throw new IllegalStateException("SseEvent sink is closed");
         }
      }
      this.eventSink = eventSink;
      //replay missed events
      if (lastEventId > -1)
      {
         synchronized (eventsStore)
         {
            if (lastEventId + 1 < eventsStore.size())
            {
               List<OutboundSseEvent> missedEvents = eventsStore.subList(lastEventId + 1, eventsStore.size());
               for (OutboundSseEvent item : missedEvents)
               {
                  this.eventSink.send(item);
               }
            }
         }
      }
   }

   @POST
   public void addMessage(final String message) throws IOException
   {
      if (eventSink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      OutboundSseEvent event = null;
      synchronized (eventsStore)
      {
         event = sse.newEventBuilder().id(Integer.toString(eventsStore.size())).data(message).build();
         eventsStore.add(event);
      }
      eventSink.send(event);
   }

   @POST
   @Path("/addMessageAndDisconnect")
   public void addMessageAndDisconnect(final String message) throws IOException, InterruptedException
   {
      //clear events store first
      eventsStore.clear();
      for (int i = 0; i < 10; i++)
      {
         OutboundSseEvent event = null;
         synchronized (eventsStore)
         {
            event = sse.newEventBuilder().id(Integer.toString(eventsStore.size())).data(i + "-" + message).build();
            eventsStore.add(event);
         }
         if (eventSink != null)
         {
            eventSink.send(event);
         }
         Thread.sleep(250);
      }
   }

   @GET
   @Path("/subscribe")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void subscribe(@Context SseEventSink sink) throws IOException
   {
      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      synchronized (this.sseBroadcasterLock) {
         //subscribe
         if (sseBroadcaster == null)
         {
            sseBroadcaster = sse.newBroadcaster();
         }
      }
      sseBroadcaster.register(sink);
   }

   @POST
   @Path("/broadcast")
   public void broadcast(String message)
   {
      if (this.sseBroadcaster == null)
      {
         throw new IllegalStateException("No Sse broadcaster created.");
      }
      ExecutorService service = (ExecutorService) servletContext
            .getAttribute(ExecutorServletContextListener.TEST_EXECUTOR);
      if ("repeat".equals(message))
      {
         service.execute(new Thread()
         {
            public void run()
            {
               for (int i = 0; i < 100; i++)
               {

                  try
                  {
                     sseBroadcaster.broadcast(sse.newEvent(message));
                     Thread.sleep(100);
                  }
                  catch (final InterruptedException e)
                  {
                     logger.error(e.getMessage(), e);
                     break;
                  }
               }
            }
         });

      }
      else
      {
         sseBroadcaster.broadcast(sse.newEvent(message));
      }
   }

   @DELETE
   public void close() throws IOException
   {
      synchronized (outputLock)
      {
         if (eventSink != null)
         {
            eventSink.close();
            eventSink = null;
         }
      }
   }

   @GET
   @Path("domains/{id}")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void startDomain(@PathParam("id") final String id, @Context SseEventSink sink)
   {
      ExecutorService service = (ExecutorService) servletContext
            .getAttribute(ExecutorServletContextListener.TEST_EXECUTOR);
      service.execute(new Thread()
      {
         public void run()
         {
            try
            {
               sink.send(sse.newEventBuilder().name("domain-progress")
                     .data(String.class, "starting domain " + id + " ...").build());
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "50%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "60%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "70%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "99%"));
               Thread.sleep(200);
               sink.send(sse.newEvent("domain-progress", "Done.")).thenAccept((Object obj) -> {
                  sink.close();
               });
            }
            catch (final InterruptedException e)
            {
               logger.error(e.getMessage(), e);
            }
         }
      });
   }

   @GET
   @Path("/events")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void eventStream(@Context SseEventSink sink) throws IOException
   {
      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      this.eventSink = sink;
      ExecutorService service = (ExecutorService) servletContext
            .getAttribute(ExecutorServletContextListener.TEST_EXECUTOR);
      service.execute(new Thread()
      {
         public void run()
         {
            while (!eventSink.isClosed() && sending)
            {
               try
               {
                  synchronized (openLock)
                  {
                     eventSink.send(sse.newEvent("msg"));
                  }
                  Thread.sleep(200);
               }
               catch (final InterruptedException e)
               {
                  logger.error(e.getMessage(), e);
                  break;
               }

            }
         }
      });
   }

   @GET
   @Path("/isopen")
   public boolean isOpen()
   {
      synchronized (openLock)
      {
         return !eventSink.isClosed();
      }

   }

   @GET
   @Path("/stopevent")
   public void stopEvent()
   {
      this.sending = false;

   }

   @GET
   @Path("/error")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void testErrorConsumer(@Context SseEventSink eventSink)
   {
      throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
   }

   @GET
   @Path("/xmlevent")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void sendXmlType(@Context SseEventSink sink)
   {
      try (SseEventSink eventSink = sink)
      {
         JAXBElement<String> element = new JAXBElement<String>(new QName("name"), String.class, "xmldata");
         eventSink.send(sse.newEventBuilder().data(element).mediaType(MediaType.APPLICATION_XML_TYPE).build());
      }
   }

   @GET
   @Path("/closeAfterSent")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void eventStream(@Context SseEventSink eventSink, @Context Sse sse) throws Exception {
      logger.info("entering eventStream()");
      ExecutorService pool = Executors.newCachedThreadPool();
      OutboundSseEvent.Builder builder = sse.newEventBuilder().mediaType(MediaType.APPLICATION_XML_TYPE);
      pool.execute(new Thread()
      {
         public void run()
         {
            try (SseEventSink sink = eventSink)
            {
               logger.info("sending 3 events");
               eventSink.send(builder.data("thing1").build());
               eventSink.send(builder.data("thing2").build());
               eventSink.send(builder.data("thing3").build());
               logger.info("sent 3 events");
            }
         }
      });
   }
   @GET
   @Path("/noContent")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void noEventStream(@Context SseEventSink eventSink) throws Exception {
      noContentCount.incrementAndGet();
      if (noContentCount.get() > 1) {
         throw new IllegalStateException("Client reconnect after http response 204");
      }
      eventSink.close();
   }

   @GET
   @Path("/bigmsg")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void bigEventMsg(@Context SseEventSink sink) throws IOException, URISyntaxException
   {
      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      this.eventSink = sink;
      ExecutorService service = (ExecutorService) servletContext
            .getAttribute(ExecutorServletContextListener.TEST_EXECUTOR);
      java.io.InputStream inputStream = SseResource.class.getResourceAsStream("bigmsg.json");
      String bigMsg = toString(new InputStreamReader(inputStream));
      service.execute(new Thread()
      {
         public void run()
         {
            if (!eventSink.isClosed() && sending)
            {
               try
               {
                  synchronized (openLock)
                  {
                     eventSink.send(sse.newEvent(bigMsg));
                  }
                  Thread.sleep(200);
               }
               catch (final InterruptedException e)
               {
                  logger.error(e.getMessage(), e);
               }

            }
         }
      });
   }
   @GET
   @Path("/json")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void jsonMessage(@Context SseEventSink sink) throws IOException, URISyntaxException
   {
      if (sink == null)
      {
         throw new IllegalStateException("No client connected.");
      }
      this.eventSink = sink;
      ExecutorService service = (ExecutorService) servletContext
            .getAttribute(ExecutorServletContextListener.TEST_EXECUTOR);
      service.execute(new Thread()
      {
         public void run()
         {
            if (!eventSink.isClosed() && sending)
            {
               try
               {
                  synchronized (openLock)
                  {
                     eventSink.send(sse.newEventBuilder().id("jsonType").
                             data(SseResource.jsonMessage).
                             mediaType(MediaType.APPLICATION_JSON_TYPE).build());
                  }
                  Thread.sleep(200);
               }
               catch (final InterruptedException e)
               {
                  logger.error(e.getMessage(), e);
               }

            }
         }
      });
   }
   public static String toString(final Reader input) throws IOException {

       final char[] buffer = new char[2048];
       StringBuilder strBuilder = new StringBuilder();
       try (Reader r = input) {
           int n = r.read(buffer);
           while (-1 != n) {
               if (n == 0) {
                   throw new IOException("0 bytes read in violation of InputStream.read(byte[])");
               }
               strBuilder.append(buffer, 0, n);
               n = r.read(buffer);
           }
           return strBuilder.toString();
       }
   }

}
