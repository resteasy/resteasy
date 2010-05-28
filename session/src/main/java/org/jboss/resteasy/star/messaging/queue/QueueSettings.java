package org.jboss.resteasy.star.messaging.queue;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueSettings
{
   protected boolean duplicatesAllowed;
   protected boolean autoAcknowledge;
   private boolean durableSend;
   private long ackTimeoutSeconds = 10;

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

   public static final QueueSettings defaultSettings;

   static
   {
      defaultSettings = new QueueSettings();
      defaultSettings.setDuplicatesAllowed(true);
      defaultSettings.setAutoAcknowledge(true);
      defaultSettings.setDurableSend(false);
   }
}
