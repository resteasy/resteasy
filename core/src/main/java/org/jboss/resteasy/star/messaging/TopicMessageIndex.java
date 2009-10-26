package org.jboss.resteasy.star.messaging;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicMessageIndex
{
   private long id = -1;
   private long next;
   private CountDownLatch latch;

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public long getNext()
   {
      return next;
   }

   public void setNext(long next)
   {
      this.next = next;
   }

   public CountDownLatch getLatch()
   {
      return latch;
   }

   public void setLatch(CountDownLatch latch)
   {
      this.latch = latch;
   }
}
