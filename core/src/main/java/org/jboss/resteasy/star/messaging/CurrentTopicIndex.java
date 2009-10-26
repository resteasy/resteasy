package org.jboss.resteasy.star.messaging;

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
      current.setId(-1);
      current.setNext(-1);
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
