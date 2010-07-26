package org.hornetq.rest.queue;

import org.hornetq.rest.Message;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueuedMessage extends Message
{
   public static enum State
   {
      POSTED,
      RECEIVED,
      ENQUEUED,
      WAITING_ON_ACKNOWLEDGEMENT,
      ACKNOWLEDGED
   }

   public static class Acknowledgement
   {
      private boolean invalidated = false;
      private boolean pulled = false;
      private CountDownLatch latch = new CountDownLatch(1);
      private boolean acknowledged = false;

      public CountDownLatch getLatch()
      {
         return latch;
      }

      public boolean pullable()
      {
         if (invalidated)
         {
            return false;
         }
         pulled = true;
         return true;
      }

      public boolean wasPulled()
      {
         return pulled;

      }

      public boolean acknowledge()
      {
         if (invalidated)
         {
            return false;
         }
         latch.countDown();
         acknowledged = true;
         return true;
      }

      public boolean isAcknowledged()
      {
         return acknowledged;
      }

      public void notAcknowledged()
      {
         latch.countDown();
      }

      public boolean invalidate()
      {
         if (latch.getCount() == 0)
         {
            return false;
         }
         invalidated = true;
         return true;
      }
   }


   private State state = State.POSTED;
   private Acknowledgement acknowledgement = new Acknowledgement();
   private String messageHref;
   private String acknowledgementHref;
   private int etag;
   private QueueStateMachine stateMachine = new QueueStateMachine();

   public QueuedMessage()
   {
   }

   public int getEtag()
   {
      return etag;
   }

   public void incrementETag()
   {
      etag++;
   }

   public QueueStateMachine getStateMachine()
   {
      return stateMachine;
   }

   public void resetStateMachine()
   {
      stateMachine = new QueueStateMachine();
   }


   public String getMessageHref()
   {
      return messageHref;
   }

   public void setMessageHref(String messageHref)
   {
      this.messageHref = messageHref;
   }

   public String getAcknowledgementHref()
   {
      return acknowledgementHref;
   }

   public void setAcknowledgementHref(String acknowledgementHref)
   {
      this.acknowledgementHref = acknowledgementHref;
   }

   public Acknowledgement getAcknowledgement()
   {
      return acknowledgement;
   }

   public Acknowledgement resetAcknowledgement()
   {
      if (this.acknowledgement == null)
      {
         this.acknowledgement = new Acknowledgement();

      }
      else
      {
         this.acknowledgement.invalidate();
         this.acknowledgement = new Acknowledgement();
      }
      return this.acknowledgement;
   }

   public State getState()
   {
      return state;
   }

   public void setState(State state)
   {
      this.state = state;
   }

}
