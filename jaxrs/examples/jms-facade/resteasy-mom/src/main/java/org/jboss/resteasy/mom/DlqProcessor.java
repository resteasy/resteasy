package org.jboss.resteasy.mom;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DlqProcessor
{
   private Connection deadletterConnection;
   private Destination dlq;

   public DlqProcessor(Connection deadletterConnection, Destination dlq) throws Exception
   {
      this.deadletterConnection = deadletterConnection;
      this.dlq = dlq;
   }

   public void close()
   {
      try
      {
         deadletterConnection.close();
      }
      catch (JMSException ignored)
      {
         ignored.printStackTrace();
      }
   }

   public void deadletter(Message message)
   {
      try
      {
         if (deadletterConnection == null || dlq == null) return;
         //System.out.println("DEAD LETTER!!!!");
         Session session = deadletterConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         try
         {
            MessageProducer producer = session.createProducer(dlq);
            producer.send(message);
            //System.out.println("SENT DEAD LETTER");
         }
         catch (JMSException e)
         {
         }
         finally
         {
            session.close();
         }
      }
      catch (JMSException ignored)
      {

      }
   }


}