package org.jboss.resteasy.star.messaging.queue;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DestinationSettings
{
   protected boolean duplicatesAllowed;
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

   public static final DestinationSettings defaultSettings;

   static
   {
      defaultSettings = new DestinationSettings();
      defaultSettings.setDuplicatesAllowed(true);
      defaultSettings.setDurableSend(false);
   }
}
