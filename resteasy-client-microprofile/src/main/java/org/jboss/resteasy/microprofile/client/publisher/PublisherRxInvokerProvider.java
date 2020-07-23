package org.jboss.resteasy.microprofile.client.publisher;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.rxjava2.i18n.Messages;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;
import java.util.concurrent.ExecutorService;

/**
 *
 */
public class PublisherRxInvokerProvider implements RxInvokerProvider<PublisherRxInvoker> {
    @Override
    public boolean isProviderFor(Class<?> clazz) {
        return PublisherRxInvoker.class.equals(clazz);
    }
    @Override
    public PublisherRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService) {
        if (syncInvoker instanceof ClientInvocationBuilder) {
            return new PublisherRxInvokerImpl(syncInvoker, executorService);
        }
        else {
            throw new ProcessingException(Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName()));
        }
    }
}
