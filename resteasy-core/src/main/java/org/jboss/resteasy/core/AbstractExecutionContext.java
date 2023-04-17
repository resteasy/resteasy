package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractExecutionContext implements ResteasyAsynchronousContext {
    protected SynchronousDispatcher dispatcher;
    protected HttpRequest request;
    protected HttpResponse response;
    private Thread initialRequestThread;

    protected AbstractExecutionContext(final SynchronousDispatcher dispatcher, final HttpRequest request,
            final HttpResponse response) {
        this.dispatcher = dispatcher;
        this.request = request;
        this.response = response;
    }

    @Override
    public void initialRequestStarted() {
        this.initialRequestThread = Thread.currentThread();
    }

    @Override
    public boolean isOnInitialRequest() {
        return initialRequestThread == Thread.currentThread();
    }

    @Override
    public void initialRequestEnded() {
        initialRequestThread = null;
    }
}
