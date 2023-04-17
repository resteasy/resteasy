package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyAsynchronousContext {
    void initialRequestStarted();

    void initialRequestEnded();

    boolean isOnInitialRequest();

    boolean isSuspended();

    ResteasyAsynchronousResponse getAsyncResponse();

    ResteasyAsynchronousResponse suspend() throws IllegalStateException;

    ResteasyAsynchronousResponse suspend(long millis) throws IllegalStateException;

    ResteasyAsynchronousResponse suspend(long time, TimeUnit unit) throws IllegalStateException;

    void complete();

    CompletionStage<Void> executeBlockingIo(RunnableWithException f, boolean hasInterceptors);

    CompletionStage<Void> executeAsyncIo(CompletionStage<Void> f);
}
