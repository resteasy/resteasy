package org.hornetq.rest.topic;

import org.hornetq.rest.queue.DestinationSettings;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicDeployment extends DestinationSettings
{
   private String name;

   public TopicDeployment()
   {
   }


   public TopicDeployment(String name, boolean duplicatesAllowed)
   {
      this.name = name;
      this.duplicatesAllowed = duplicatesAllowed;
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