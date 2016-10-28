package org.jboss.resteasy.test.cdi.injection.resource;


import org.jboss.resteasy.test.cdi.util.Counter;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@MessageDriven(name = "BookMDB",
        activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                            @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test")}
)
public class CDIInjectionBookMDB implements MessageListener {
    @Inject
    private Logger log;
    @Inject
    private CDIInjectionBookResource resource;

    public void onMessage(Message arg0) {
        TextMessage tm = TextMessage.class.cast(arg0);
        try {
            log.info(this + ": msg: " + tm.getText());
            CDIInjectionBookCollection collection = resource.getBookCollection();
            log.info(this + ": collection.size() before: " + collection.getBooks().size());
            Counter counter = resource.getCounter();
            collection.addBook(new CDIInjectionBook(counter.getNext(), tm.getText()));
            log.info(this + ": collection.size() after: " + collection.getBooks().size());
            CountDownLatch latch = resource.getCountDownLatch();
            latch.countDown(); // Tell BookResource book has been stored.
        } catch (JMSException e) {
            log.info(String.format("Stacktrace: %s", (Object[]) e.getStackTrace()));
        }
    }

}

