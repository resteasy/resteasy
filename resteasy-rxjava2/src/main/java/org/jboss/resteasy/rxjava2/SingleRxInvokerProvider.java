package org.jboss.resteasy.rxjava2;

import java.util.concurrent.ExecutorService;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.rxjava2.i18n.Messages;

public class SingleRxInvokerProvider implements RxInvokerProvider<SingleRxInvoker> {
    @Override
    public boolean isProviderFor(Class<?> clazz) {
        return SingleRxInvoker.class.equals(clazz);
    }

    @Override
    public SingleRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService) {
        if (syncInvoker instanceof ClientInvocationBuilder) {
            return new SingleRxInvokerImpl((ClientInvocationBuilder) syncInvoker);
        }
        throw new ProcessingException(Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName()));
    }
}
