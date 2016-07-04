package org.jboss.resteasy.cdi.injection;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.resteasy.cdi.util.Counter;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 28, 2012
 */
@MessageDriven(name = "BookMDB",
               activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                                   @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test")}
)
public class BookMDB implements MessageListener
{
   @Inject private Logger log;
   @Inject private BookResource resource;
   
   public void onMessage(Message arg0)
   {
      TextMessage tm = TextMessage.class.cast(arg0);
      try
      {
         log.info(this + ": msg: " + tm.getText());
         BookCollection collection = resource.getBookCollection();
         log.info(this + ": collection.size() before: " + collection.getBooks().size());
         Counter counter = resource.getCounter();
         collection.addBook(new Book(counter.getNext(), tm.getText()));
         log.info(this + ": collection.size() after: " + collection.getBooks().size());
         CountDownLatch latch = resource.getCountDownLatch();
         latch.countDown(); // Tell BookResource book has been stored.
      }
      catch (JMSException e)
      {
         e.printStackTrace();
      }
   }

}

