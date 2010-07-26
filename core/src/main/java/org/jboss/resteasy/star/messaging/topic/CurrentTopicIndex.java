package org.hornetq.rest.topic;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CurrentTopicIndex
{
   private TopicMessageIndex current;

   public CurrentTopicIndex()
   {
      current = new TopicMessageIndex();
      current.setId(null);
      current.setNext(null);
      current.setLatch(new CountDownLatch(1));
   }

   public synchronized TopicMessageIndex getCurrent()
   {
      return current;
   }

   public synchronized void setCurrent(TopicMessageIndex current)
   {
      this.current = current;
   }
}
