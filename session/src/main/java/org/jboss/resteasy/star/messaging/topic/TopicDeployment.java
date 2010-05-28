package org.jboss.resteasy.star.messaging.topic;

import org.jboss.resteasy.star.messaging.queue.QueueSettings;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicDeployment extends QueueSettings
{
   private String name;

   public TopicDeployment()
   {
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

}