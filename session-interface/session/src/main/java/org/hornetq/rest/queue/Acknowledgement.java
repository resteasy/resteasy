package org.hornetq.rest.queue;

import org.hornetq.api.core.client.ClientMessage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Acknowledgement
{
   private final String ackToken;
   private final ClientMessage message;
   private boolean wasSet;
   private boolean acknowledged;

   public Acknowledgement(String ackToken, ClientMessage message)
   {
      this.ackToken = ackToken;
      this.message = message;
   }

   public String getAckToken()
   {
      return ackToken;
   }

   public ClientMessage getMessage()
   {
      return message;
   }

   public boolean wasSet()
   {
      return wasSet;
   }

   public void acknowledge()
   {
      if (wasSet) throw new RuntimeException("Ack state is immutable");
      wasSet = true;
      acknowledged = true;
   }

   public void unacknowledge()
   {
      if (wasSet) throw new RuntimeException("Ack state is immutable");
      wasSet = true;
   }

   public boolean isAcknowledged()
   {
      return acknowledged;
   }

}