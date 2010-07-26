package org.hornetq.rest.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.rest.util.HttpMessageHelper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

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
