package org.jboss.resteasy.mom;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
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
public class QueueResource extends DestinationResource
{
   protected QueueReceiver autoReceiver;
   protected Map<String, Receiver> receivers = new ConcurrentHashMap<String, Receiver>();
   protected Map<String, Listener> listeners = new ConcurrentHashMap<String, Listener>();
   protected DlqProcessor dlq;

   public QueueResource(String name, Connection connection, Destination destination, MessageProcessor processor, DlqProcessor dlq) throws Exception
   {
      super(processor, name, connection, destination);
      this.dlq = dlq;
   }

   @Path("/browser")
   public QueueBrowserResource getBrowser() throws Exception
   {
      return new QueueBrowserResource(connection, (Queue) destination, processor);
   }


   @POST
   @Path("/head")
   public synchronized Response getAndAcknowledge(@QueryParam("wait") @DefaultValue("-1")long wait) throws Exception
   {
      if (receivers.size() > 0) return Response.status(HttpResponseCodes.SC_CONFLICT).build();
      if (autoReceiver == null) autoReceiver = new QueueReceiver(destination, connection, processor, "");
      return autoReceiver.getAndAcknowledge(wait);
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
         QueueReceiver receiver = new QueueReceiver(destination, connection, processor, processor.createSelector(headers));
         receivers.put(id, receiver);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
   }

   @Path("/receivers/{id}")
   public QueueReceiver getReceiver(@PathParam("id")String id)
   {
      //System.out.println("getReceiver: " + id);
      QueueReceiver receiver = (QueueReceiver) receivers.get(id);
      if (receiver == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      return receiver;
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
         Listener listener = new QueueListener(destination, connection, uri, processor, dlq, processor.createSelector(headers));
         listeners.put(id, listener);
         return Response.status(HttpResponseCodes.SC_CREATED).build();
      }
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

   @DELETE
   @Path("/listeners/{id}")
   public Response closeListener(@PathParam("id")String id) throws Exception
   {
      Listener listener = listeners.remove(id);
      if (listener == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      listener.close();
      return Response.status(HttpResponseCodes.SC_NO_CONTENT).build();
   }

}
