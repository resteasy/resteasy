package org.jboss.resteasy.star.messaging.topic;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicDeployment
{
   private String name;
   private boolean duplicatesAllowed;
   private boolean autoAcknowledge;
   private boolean durableSend;
   private long ackTimeoutSeconds = 10;

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

   public boolean isDuplicatesAllowed()
   {
      return duplicatesAllowed;
   }

   public void setDuplicatesAllowed(boolean duplicatesAllowed)
   {
      this.duplicatesAllowed = duplicatesAllowed;
   }

   public boolean isAutoAcknowledge()
   {
      return autoAcknowledge;
   }

   public void setAutoAcknowledge(boolean autoAcknowledge)
   {
      this.autoAcknowledge = autoAcknowledge;
   }

   public long getAckTimeoutSeconds()
   {
      return ackTimeoutSeconds;
   }

   public void setAckTimeoutSeconds(long ackTimeoutSeconds)
   {
      this.ackTimeoutSeconds = ackTimeoutSeconds;
   }

   public boolean isDurableSend()
   {
      return durableSend;
   }

   public void setDurableSend(boolean durableSend)
   {
      this.durableSend = durableSend;
   }
}