package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.client.ClientMessage;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AckStateMachine
{
   protected CountDownLatch ack = new CountDownLatch(1);
   protected CountDownLatch ackAck = new CountDownLatch(1);
   private boolean invalidated = false;
   private boolean acknowledged = false;
   private boolean unacknowledged = false;
   private final String ackToken;
   private final ClientMessage message;

   public AckStateMachine(String ackToken, ClientMessage message)
   {
      this.ackToken = ackToken;
      this.message = message;
   }

   public boolean isAcknowledged()
   {
      return acknowledged;
   }

   public CountDownLatch getAck()
   {
      return ack;
   }

   public CountDownLatch getAckAck()
   {
      return ackAck;
   }

   public boolean isInvalidated()
   {
      return invalidated;
   }

   public boolean isUnacknowledged()
   {
      return unacknowledged;
   }

   public String getAckToken()
   {
      return ackToken;
   }

   public ClientMessage getMessage()
   {
      return message;
   }

   public void invalidate()
   {
      invalidated = true;
      ackAck.countDown();
   }

   public void acknowledge()
   {
      acknowledged = true;
      ackAck.countDown();
   }

   public void unacknowledgeSend()
   {
      unacknowledged = true;
      ack.countDown();
   }


   public boolean acknowledgeSend(String token)
   {
      if (invalidated) return false;
      if (!ackToken.equals(token)) return false;

      ack.countDown();
      try
      {
         ackAck.await();
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
      return acknowledged;

   }
}