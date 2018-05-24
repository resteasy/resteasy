package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.logging.Logger;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

@ApplicationScoped
@Resource(name = "java:/jms/queue/test")
public class CDIInjectionResourceProducer {
    private static Connection connection;
    private static Session session;
    private static boolean disposed;
    @Resource(mappedName = "java:/jms/queue/test")
    Queue bookQueue;
    @Resource(mappedName = "java:jboss/exported/jms/RemoteConnectionFactory")
    ConnectionFactory connectionFactory;

    protected static Logger log = Logger.getLogger(CDIInjectionResourceProducer.class);

    public static void dispose(@Disposes @CDIInjectionResourceBinding Queue queue) {
        log.info("ResourceProducer.dispose() called");
        disposed = true;
    }

    public static boolean isDisposed() {
        return disposed;
    }

    @Produces
    @CDIInjectionResourceBinding
    public Queue toDestination() {
        log.info("Queue: " + bookQueue);
        return bookQueue;
    }

    @Produces
    @CDIInjectionResourceBinding
    public Session getSession() throws JMSException {
        if (connection == null) {
            connection = connectionFactory.createConnection("guest", "pass");
            connection.start();
        }
        if (session == null) {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        return session;
    }

    @PreDestroy
    public void preDestroy() throws JMSException {
        if (connection != null) {
            connection.close();
            log.info("ResourceProducer.preDestory(): closed connection");
        }
    }
}
