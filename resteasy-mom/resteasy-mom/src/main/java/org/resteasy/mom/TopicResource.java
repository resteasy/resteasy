package org.resteasy.mom;

import org.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicResource extends DestinationResource
{
   protected ConnectionFactory factory;

   protected Map<String, DurableTopicReceiver> durableReceivers = new ConcurrentHashMap<String, DurableTopicReceiver>();
   protected Map<String, TopicReceiver> nondurableReceivers = new ConcurrentHashMap<String, TopicReceiver>();
   protected Map<String, DurableTopicListener> durableListeners = new ConcurrentHashMap<String, DurableTopicListener>();
   protected Map<String, TopicListener> nondurableListeners = new ConcurrentHashMap<String, TopicListener>();
   protected DlqProcessor dlq;

   public TopicResource(String name, ConnectionFactory factory, Connection connection, Destination destination, MessageProcessor processor, DlqProcessor dlq) throws Exception
   {
      super(processor, name, connection, destination);
      this.factory = factory;
      this.dlq = dlq;
   }

   public void close() throws Exception
   {
      for (DurableTopicReceiver receiver : durableReceivers.values())
      {
         try
         {
            receiver.close(false);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      for (DurableTopicListener receiver : durableListeners.values())
      {
         try
         {
            receiver.close(false);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      super.close();
   }


   @Path("/durable/receivers/{id}")
   public DurableTopicReceiver getDurableReceiver(@PathParam("id")String id)
   {
      id = "durable/receivers/" + id;
      //System.out.println("getDurableReceiver: " + id);
      DurableTopicReceiver receiver = durableReceivers.get(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      return receiver;
   }

   @PUT
   @Path("/durable/receivers/{id}")
   public Response createDurableReceiver(@PathParam("id")String id,
                                         @Context HttpHeaders headers) throws Exception
   {
      id = "durable/receivers/" + id;
      if (durableReceivers.containsKey(id))
      {
         return Response.ok().build();
      }
      else
      {
         //System.out.println("Creating durable receiver: " + id);
         Connection connection = factory.createConnection();
         connection.setClientID(id);
         DurableTopicReceiver receiver = null;
         try
         {
            receiver = new DurableTopicReceiver(id, connection, destination, processor, processor.createSelector(headers));
         }
         catch (Exception e)
         {
            try { connection.close(); } catch (Exception ignored) {}
            throw e;
         }
         durableReceivers.put(id, receiver);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
   }

   @DELETE
   @Path("/durable/receivers/{id}")
   public Response closeDurableReceiver(@PathParam("id")String id,
                                        @QueryParam("unsubscribe") @DefaultValue("true")boolean unsubscribe) throws Exception
   {
      id = "durable/receivers/" + id;
      DurableTopicReceiver receiver = durableReceivers.remove(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      receiver.close(unsubscribe);
      return Response.status(HttpResponseCodes.SC_NO_CONTENT).build();
   }

   @PUT
   @Path("/durable/listeners/{id}")
   public Response createDurableListener(@PathParam("id")String id,
                                         String callback,
                                         @Context HttpHeaders headers) throws Exception
   {
      id = "durable/listeners/" + id;
      if (durableListeners.containsKey(id))
      {
         return Response.ok().build();
      }
      else
      {
         //System.out.println("Creating durable listener: " + id + " callback uri: " + callback);

         Connection connection = factory.createConnection();
         connection.setClientID(id);
         DurableTopicListener receiver = null;
         try
         {
            receiver = new DurableTopicListener(id, destination, connection, callback, processor, dlq, processor.createSelector(headers));
         }
         catch (Exception e)
         {
            try { connection.close(); } catch (Exception ignored) {}
            throw e;
         }
         durableListeners.put(id, receiver);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
   }

   @DELETE
   @Path("/durable/listeners/{id}")
   public Response closeDurableListener(@PathParam("id")String id,
                                        @QueryParam("unsubscribe") @DefaultValue("true")boolean unsubscribe) throws Exception
   {
      id = "durable/listeners/" + id;
      DurableTopicListener receiver = durableListeners.remove(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      receiver.close(unsubscribe);
      return Response.status(HttpResponseCodes.SC_NO_CONTENT).build();
   }

   @PUT
   @Path("/receivers/{id}")
   public Response createReceiver(@PathParam("id")String id,
                                  @Context HttpHeaders headers) throws Exception
   {
      if (nondurableReceivers.containsKey(id))
      {
         return Response.ok().build();
      }
      else
      {
         TopicReceiver receiver = new TopicReceiver(connection, destination, processor, processor.createSelector(headers));
         nondurableReceivers.put(id, receiver);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
   }

   @DELETE
   @Path("/receivers/{id}")
   public Response closeReceiver(@PathParam("id")String id) throws Exception
   {
      Receiver receiver = nondurableReceivers.remove(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      receiver.close();
      return Response.status(HttpResponseCodes.SC_NO_CONTENT).build();
   }

   @Path("/receivers/{id}")
   public TopicReceiver getReceiver(@PathParam("id")String id)
   {
      //System.out.println("getTopicReceiver: " + id);
      TopicReceiver receiver = (TopicReceiver) nondurableReceivers.get(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      return receiver;
   }

   @PUT
   @Path("/listeners/{id}")
   public Response createListener(@PathParam("id")String id, String uri,
                                  @Context HttpHeaders headers) throws Exception
   {
      if (nondurableListeners.containsKey(id))
      {
         return Response.ok().build();
      }
      else
      {
         TopicListener listener = new TopicListener(destination, connection, uri, processor, dlq, processor.createSelector(headers));
         nondurableListeners.put(id, listener);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
   }


   @DELETE
   @Path("/listeners/{id}")
   public Response closeListener(@PathParam("id")String id) throws Exception
   {
      TopicListener receiver = nondurableListeners.remove(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      receiver.close();
      return Response.status(HttpResponseCodes.SC_NO_CONTENT).build();
   }

}