package org.resteasy.mom;

import org.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueResource
{
   private Connection connection;
   private Destination destination;
   private Map<String, Receiver> receivers = new ConcurrentHashMap<String, Receiver>();
   private Map<String, Listener> listeners = new ConcurrentHashMap<String, Listener>();
   private String name;
   private Receiver autoReceiver;
   private MessageProcessor processor;

   public QueueResource(String name, Connection connection, Destination destination, MessageProcessor processor) throws Exception
   {
      this.destination = destination;
      this.name = name;
      this.connection = connection;
      this.processor = processor;
   }

   @GET
   @ProduceMime("text/html")
   public String details()
   {
      return "<html><body><h1>Queue: " + name + "</h1></body></html>";
   }

   @Path("/browser")
   public QueueBrowserResource getBrowser(@QueryParam("index") @DefaultValue("0")int index, @Context UriInfo info) throws Exception
   {
      return new QueueBrowserResource(connection, (Queue) destination, processor);
   }


   @POST
   public void send(@Context HttpHeaders headers,
                    @QueryParam("persistent") @DefaultValue("true")boolean persistent,
                    @QueryParam("priority") @DefaultValue("4")int priority,
                    @QueryParam("timeToLive") @DefaultValue("0")long timeToLive,
                    InputStream entityStream) throws Exception
   {
      System.out.println("SENDING MESSAGE!");
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      try
      {
         MessageProducer producer = session.createProducer(destination);
         try
         {
            Message message = processor.createMessage(headers, entityStream, session);
            int deliveryMode = persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
            producer.send(message, deliveryMode, priority, timeToLive);
         }
         finally
         {
            producer.close();
         }
      }
      finally
      {
         session.close();
      }
   }


   public void close() throws Exception
   {
      connection.close();
   }

   @POST
   @Path("/head")
   public synchronized Response getAndAcknowledge(@QueryParam("wait") @DefaultValue("-1")long wait) throws Exception
   {
      if (receivers.size() > 0) return Response.status(HttpResponseCodes.SC_CONFLICT).build();
      if (autoReceiver == null) autoReceiver = new Receiver(destination, connection, processor, "");
      return autoReceiver.getAndAcknowledge(wait);
   }


   @DELETE
   @Path("/receivers/{id}")
   public Response closeReceiver(@PathParam("id")String id) throws Exception
   {
      Receiver receiver = receivers.remove(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      receiver.close();
      return Response.status(HttpResponseCodes.SC_NO_CONTENT).build();
   }

   @PUT
   @Path("/receivers/{id}")
   public Response createReceiver(@PathParam("id")String id, @Context HttpHeaders headers) throws Exception
   {
      if (receivers.containsKey(id))
      {
         return Response.ok().build();
      }
      else
      {
         Receiver receiver = new Receiver(destination, connection, processor, processor.createSelector(headers));
         receivers.put(id, receiver);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
   }

   @Path("/receivers/{id}")
   public Receiver getReceiver(@PathParam("id")String id)
   {
      System.out.println("getReceiver: " + id);
      Receiver receiver = receivers.get(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      return receiver;
   }

   @DELETE
   @Path("/listeners/{id}")
   public Response closeListener(@PathParam("id")String id) throws Exception
   {
      Listener listener = listeners.remove(id);
      if (listener == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      listener.close();
      return Response.status(HttpResponseCodes.SC_NO_CONTENT).build();
   }

   @PUT
   @Path("/listeners/{id}")
   public Response createListener(@PathParam("id")String id, String uri, @Context HttpHeaders headers) throws Exception
   {
      if (listeners.containsKey(id))
      {
         return Response.ok().build();
      }
      else
      {
         Listener listener = new Listener(destination, connection, uri, processor, processor.createSelector(headers));
         listeners.put(id, listener);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
   }

}
