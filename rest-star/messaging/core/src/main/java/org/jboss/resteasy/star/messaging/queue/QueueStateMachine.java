package org.jboss.resteasy.star.messaging.queue;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueStateMachine
{
   protected CountDownLatch queuePolled = new CountDownLatch(1);
   protected CountDownLatch waitOnAcknowledgement = new CountDownLatch(1);
   protected CountDownLatch acknowledgement = new CountDownLatch(1);
   private boolean invalidated = false;
   private boolean acknowledged = false;


   public CountDownLatch getQueuePolled()
   {
      return queuePolled;
   }

   public CountDownLatch getWaitOnAcknowledgement()
   {
      return waitOnAcknowledgement;
   }

   public CountDownLatch getAcknowledgement()
   {
      return acknowledgement;
   }

   public boolean isAcknowledged()
   {
      return acknowledged;
   }

   public void unacknowledge()
   {
      acknowledgement.countDown();
   }

   public boolean acknowledge()
   {
      if (invalidated)
      {
         return false;
      }
      acknowledgement.countDown();
      acknowledged = true;
      return true;

   }
}
