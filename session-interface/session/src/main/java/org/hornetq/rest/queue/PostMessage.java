package org.hornetq.rest.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.rest.util.HttpMessageHelper;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMessage
{
   protected ClientSessionFactory sessionFactory;
   protected String destination;
   protected boolean defaultDurable = false;
   protected DestinationServiceManager serviceManager;
   private AtomicLong counter = new AtomicLong(1);
   private final String startupTime = Long.toString(System.currentTimeMillis());

   protected String generateDupId()
   {
      return startupTime + Long.toString(counter.incrementAndGet());
   }

   public void publish(HttpHeaders headers, byte[] body, String dup, boolean durable) throws Exception
   {
      Pooled pooled = getPooled();
      try
      {
         ClientProducer producer = pooled.producer;
         ClientMessage message = createHornetQMessage(headers, body, durable, pooled.session);
         message.putStringProperty(ClientMessage.HDR_DUPLICATE_DETECTION_ID.toString(), dup);
         producer.send(message);
         pool.add(pooled);
      }
      catch (Exception ex)
      {
         try
         {
            pooled.session.close();
         }
         catch (HornetQException e)
         {
         }
         addPooled();
         throw ex;
      }
   }

   @PUT
   @Path("{id}")
   public Response putWithId(@PathParam("id") String dupId, @QueryParam("durable") Boolean durable, @Context HttpHeaders headers, @Context UriInfo uriInfo, byte[] body)
   {
      return postWithId(dupId, durable, headers, uriInfo, body);
   }

   @POST
   @Path("{id}")
   public Response postWithId(@PathParam("id") String dupId, @QueryParam("durable") Boolean durable, @Context HttpHeaders headers, @Context UriInfo uriInfo, byte[] body)
   {
      String matched = uriInfo.getMatchedURIs().get(1);
      UriBuilder nextBuilder = uriInfo.getBaseUriBuilder();
      String nextId = generateDupId();
      nextBuilder.path(matched).path(nextId);
      URI next = nextBuilder.build();

      boolean isDurable = defaultDurable;
      if (durable != null)
      {
         isDurable = durable.booleanValue();
      }
      try
      {
         publish(headers, body, dupId, isDurable);
      }
      catch (Exception e)
      {
         Response error = Response.serverError()
                 .entity("Problem posting message: " + e.getMessage())
                 .type("text/plain")
                 .build();
         throw new WebApplicationException(e, error);
      }
      Response.ResponseBuilder builder = Response.status(201);
      serviceManager.getLinkStrategy().setLinkHeader(builder, "create-next", "create-next", next.toString(), "*/*");
      return builder.build();
   }


   protected static class Pooled
   {
      public ClientSession session;
      public ClientProducer producer;

      private Pooled(ClientSession session, ClientProducer producer)
      {
         this.session = session;
         this.producer = producer;
      }
   }

   protected ArrayBlockingQueue<Pooled> pool;
   protected int poolSize = 10;

   public DestinationServiceManager getServiceManager()
   {
      return serviceManager;
   }

   public void setServiceManager(DestinationServiceManager serviceManager)
   {
      this.serviceManager = serviceManager;
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

   public boolean isDefaultDurable()
   {
      return defaultDurable;
   }

   public void setDefaultDurable(boolean defaultDurable)
   {
      this.defaultDurable = defaultDurable;
   }

   public int getPoolSize()
   {
      return poolSize;
   }

   public void setPoolSize(int poolSize)
   {
      this.poolSize = poolSize;
   }

   public void init() throws Exception
   {
      pool = new ArrayBlockingQueue<Pooled>(poolSize);
      for (int i = 0; i < poolSize; i++)
      {
         addPooled();
      }
   }

   protected void addPooled()
           throws HornetQException
   {
      ClientSession session = sessionFactory.createSession();
      ClientProducer producer = session.createProducer(destination);
      session.start();
      pool.add(new Pooled(session, producer));
   }

   protected Pooled getPooled()
           throws InterruptedException
   {
      Pooled pooled = pool.poll(1, TimeUnit.SECONDS);
      if (pooled == null)
      {
         throw new WebApplicationException(Response.status(503).entity("Timed out waiting for available producer.").type("text/plain").build());
      }
      return pooled;
   }

   public void cleanup()
   {
      for (Pooled pooled : pool)
      {
         try
         {
            pooled.session.close();
         }
         catch (HornetQException e)
         {
            throw new RuntimeException(e);
         }
      }
   }


   protected ClientMessage createHornetQMessage(HttpHeaders headers, byte[] body, boolean durable, ClientSession session) throws Exception
   {
      ClientMessage message = session.createMessage(durable);
      HttpMessageHelper.writeHttpMessage(headers, body, message);
      return message;
   }
}
