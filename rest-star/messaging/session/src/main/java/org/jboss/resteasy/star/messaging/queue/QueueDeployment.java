package org.jboss.resteasy.star.messaging.queue;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueDeployment extends QueueSettings
{
   private String name;

   public QueueDeployment()
   {
   }

   public QueueDeployment(String name, boolean duplicatesAllowed, boolean autoAcknowledge)
   {
      this.name = name;
      this.duplicatesAllowed = duplicatesAllowed;
      this.autoAcknowledge = autoAcknowledge;
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