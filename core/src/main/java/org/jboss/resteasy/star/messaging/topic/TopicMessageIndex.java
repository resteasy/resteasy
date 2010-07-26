package org.hornetq.rest.topic;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicMessageIndex
{
   private String id;
   private String next;
   private CountDownLatch latch;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getNext()
   {
      return next;
   }

   public void setNext(String next)
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
