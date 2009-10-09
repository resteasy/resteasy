package org.jboss.resteasy.star.messaging;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CurrentMessageIndex
{
   private MessageIndex current;

   public CurrentMessageIndex()
   {
      current = new MessageIndex();
      current.setId(-1);
      current.setNext(-1);
      current.setLatch(new CountDownLatch(1));
   }

   public synchronized MessageIndex getCurrent()
   {
      return current;
   }

   public synchronized void setCurrent(MessageIndex current)
   {
      this.current = current;
   }
}
