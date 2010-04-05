package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.MessageHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Sync issues:
 * 1. Listener is woken up by acknowledgement/cancel from client
 * 2. Client never acknowledges
 * 3. Listener comes out of awaiting lock before acknowledgement
 * 4. Listener comes out of awaiting lock after acknowledgement
 * 5. Client acknowledges too late
 * 6. Client acknowledges after a server crash
 * 7. message is never pulled
 * 8. listener comes out of awaiting before poller returns
 * 9. How do you know if the message is enqueued or not?
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueListener implements MessageHandler
{
   protected LinkedBlockingQueue<QueuedMessage> queue;
   protected QueueMessageRepository repository;
   protected long timeOut = 1;
   protected TimeUnit timeOutUnit = TimeUnit.MINUTES;
   protected boolean shutdown = false;

   public QueueListener(LinkedBlockingQueue<QueuedMessage> queue, QueueMessageRepository repository)
   {
      this.queue = queue;
      this.repository = repository;
   }

   public long getTimeOut()
   {
      return timeOut;
   }

   public void setTimeOut(long timeOut)
   {
      this.timeOut = timeOut;
   }

   public TimeUnit getTimeOutUnit()
   {
      return timeOutUnit;
   }

   public void setTimeOutUnit(TimeUnit timeOutUnit)
   {
      this.timeOutUnit = timeOutUnit;
   }

   public void stop()
   {
      shutdown = true;
   }


   public void onMessage(ClientMessage notification)
   {
      long id = notification.getLongProperty("m-id");
      System.out.println("onMessage: " + id);
      QueuedMessage message = repository.getMessage(id);
      if (message == null)
      {
         System.out.println("MESSAGE WAS NULL!!!");
         // todo, acknowledge and put in dead letter queue
      }
      synchronized (message)
      {
         try
         {
            queue.put(message);
            message.setState(QueuedMessage.State.ENQUEUED);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException(e);
         }
      }
      do
      {
         try
         {
            // await for message to be pulled from queue
            // I think it is safe to wait forever here.
            message.getStateMachine().getQueuePolled().await();
            boolean acquired = message.getStateMachine().getAcknowledgement().await(timeOut, timeOutUnit);
            synchronized (message)
            {
               if (message.getStateMachine().isAcknowledged())
               {
                  System.out.println("Acknowledging: " + id);
                  message.setState(QueuedMessage.State.ACKNOWLEDGED);
                  notification.acknowledge();
                  break;
               }
               else
               {
                  message.incrementETag();
                  message.resetStateMachine();
                  message.setState(QueuedMessage.State.ENQUEUED);
                  queue.put(message);

               }
            }
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException(e);
         }
         catch (HornetQException e)
         {
            throw new RuntimeException(e);
         }


      } while (!shutdown);

   }


}
