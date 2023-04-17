package org.jboss.resteasy.test.asyncio;

import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;

public class MyAsyncWriterInterceptor extends BlockingWriterInterceptor implements AsyncWriterInterceptor {

    @Override
    public CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context) {
        context.getHeaders().add("X-Writer", "async");
        return context.asyncProceed();
    }

}
