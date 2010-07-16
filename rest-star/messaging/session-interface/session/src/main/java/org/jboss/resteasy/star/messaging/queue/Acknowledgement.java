package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.client.ClientMessage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Acknowledgement
{
   private final String ackToken;
   private final ClientMessage message;

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

}