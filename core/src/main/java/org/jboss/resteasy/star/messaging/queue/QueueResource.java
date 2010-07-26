package org.hornetq.rest.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.spi.Link;
import org.hornetq.rest.Constants;
import org.hornetq.rest.LinkHeaderSupport;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueResource
{
   protected LinkedBlockingQueue<QueuedMessage> queue = new LinkedBlockingQueue<QueuedMessage>();
   protected QueueMessageRepository repository = new QueueMessageRepository();
   protected ClientSessionFactory sessionFactory;
   protected String destination;
   protected Object sender;
   protected int numConsumers = 5;
   protected List<ClientSession> consumers = new ArrayList<ClientSession>();

   public void start() throws Exception
   {
      for (int i = 0; i < numConsumers; i++)
      {
         ClientSession session = sessionFactory.createSession();
         QueueListener listener = new QueueListener(queue, repository);
         session.createConsumer(destination).setMessageHandler(listener);
         consumers.add(session);
         session.start();

      }
   }

   public void stop()
   {
      for (ClientSession session : consumers)
      {
         try
         {
            session.close();
         }
         catch (HornetQException ignored)
         {
         }
      }
   }

   public Object getSender()
   {
      return sender;
   }

   public void setSender(Object sender)
   {
      this.sender = sender;
   }

   @GET
   @Produces("application/xml")
   public Response get(@Context UriInfo uriInfo)
   {


      String msg = "<queue>"
              + "<name>" + destination + "</name>"
              + "</queue/>";
      Response.ResponseBuilder builder = Response.ok(msg);
      setSenderLink(builder, uriInfo);
      setSenderLink(builder, uriInfo);
      setPollerLink(builder, uriInfo);
      return builder.build();
   }

   @HEAD
   @Produces("application/xml")
   public Response head(@Context UriInfo uriInfo)
   {
      Response.ResponseBuilder builder = Response.ok();
      setSenderLink(builder, uriInfo);
      setPollerLink(builder, uriInfo);
      return builder.build();
   }

   protected void setSenderLink(Response.ResponseBuilder response, UriInfo info)
   {
      UriBuilder builder = info.getRequestUriBuilder();
      builder.path("create");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "create", "create", uri, null);
   }

   protected void setPollerLink(Response.ResponseBuilder response, UriInfo info)
   {
      UriBuilder builder = info.getRequestUriBuilder();
      builder.path("consume-next");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "consume-next", "consume-next", uri, null);
   }


   public int getNumConsumers()
   {
      return numConsumers;
   }

   public void setNumConsumers(int numConsumers)
   {
      this.numConsumers = numConsumers;
   }

   public LinkedBlockingQueue<QueuedMessage> getQueue()
   {
      return queue;
   }

   public void setQueue(LinkedBlockingQueue<QueuedMessage> queue)
   {
      this.queue = queue;
   }

   public QueueMessageRepository getRepository()
   {
      return repository;
   }

   public void setRepository(QueueMessageRepository repository)
   {
      this.repository = repository;
   }

   public ClientSessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public void setSessionFactory(ClientSessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   @Path("create")
   public Object post() throws Exception
   {
      return sender;
   }

   @Path("consume-next")
   @POST
   public Response poll(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                        @Context UriInfo info)
   {
      try
      {
         QueuedMessage message = queue.poll(wait, TimeUnit.SECONDS);
         if (message == null)
         {
            return Response.status(503).build();
         }
         synchronized (message)
         {
            message.getStateMachine().getQueuePolled().countDown();
            message.setState(QueuedMessage.State.WAITING_ON_ACKNOWLEDGEMENT);
            Response response = getMessageResponse(message, info).build();
            return response;
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Path("messages/{id}/state")
   @POST
   public Response acknowledge(
           @PathParam("id") String id,
           @FormParam("acknowledge") boolean acknowledge,
           @Context Request request)
   {
      QueuedMessage message = repository.getMessage(id);
      if (message == null)
      {
         throw new WebApplicationException(405); // gone
      }
      synchronized (message)
      {
         int attempt = message.getEtag();
         EntityTag etag = new EntityTag(Integer.toString(attempt));
         Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
         if (builder != null)
         {
            return builder.build();
         }

         if (acknowledge)
         {
            System.out.println("Ack received for: " + id);
            boolean acknowledged = message.getStateMachine().acknowledge();
            if (acknowledged)
            {
               return Response.noContent().build();
            }
            else
            {
               return Response.status(Response.Status.PRECONDITION_FAILED.getStatusCode()).build();
            }
         }
         else
         {
            System.out.println("UnAck received for: " + id);
            message.getStateMachine().unacknowledge();
            return Response.noContent().build();
         }
      }
   }


   @Path("messages/{id}")
   @GET
   public Response getMessage(@PathParam("id") long id)
   {
      return null;
   }

   protected Response.ResponseBuilder getMessageResponse(QueuedMessage msg, UriInfo info)
   {
      Response.ResponseBuilder responseBuilder = Response.ok();
      setAcknowledgementLink(responseBuilder, info, msg);
      for (String header : msg.getHeaders().keySet())
      {
         List values = msg.getHeaders().get(header);
         for (Object value : values) responseBuilder.header(header, value);
      }
      if (msg.getBody() != null)
      {
         responseBuilder.entity(msg.getBody());
         String type = msg.getHeaders().getFirst("Content-Type");
         responseBuilder.type(type);
      }
      else
      {
         responseBuilder.status(204);
      }
      return responseBuilder;
   }

   protected void setAcknowledgementLink(Response.ResponseBuilder response, UriInfo info, QueuedMessage msg)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("messages/" + msg.getId() + "/state");
      String uri = builder.build().toString();
      Link link = new Link("acknowledgement", "acknowledgement", uri, MediaType.APPLICATION_FORM_URLENCODED, null);
      link.getExtensions().add("etag", Integer.toString(msg.getEtag()));
      LinkHeaderSupport.setLinkHeader(response, link);
   }

}
