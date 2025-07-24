package org.jboss.resteasy.test.cdi.injection.resource;

import java.util.logging.Logger;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(name = "BookMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test") })
public class CDIInjectionBookMDB implements MessageListener {
    @Inject
    private Logger log;
    @EJB
    private CDIInjectionBookCollection collection; // application scoped singleton: injected as EJB proxy

    public void onMessage(Message arg0) {
        TextMessage tm = TextMessage.class.cast(arg0);
        try {
            log.info(this + ": msg: " + tm.getText());
            log.info(this + ": collection.size() before: " + collection.getBooks().size());
            collection.addBook(new CDIInjectionBook(tm.getText()));
            log.info(this + ": collection.size() after: " + collection.getBooks().size());
        } catch (JMSException e) {
            log.info(String.format("Stacktrace: %s", (Object[]) e.getStackTrace()));
        }
    }

}
