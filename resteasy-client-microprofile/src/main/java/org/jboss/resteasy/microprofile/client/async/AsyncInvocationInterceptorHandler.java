package org.jboss.resteasy.microprofile.client.async;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptor;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class AsyncInvocationInterceptorHandler {

    static final ThreadLocal<Collection<AsyncInvocationInterceptor>> threadBoundInterceptors = new ThreadLocal<>();

    public static void register(Collection<AsyncInvocationInterceptor> interceptor) {
        threadBoundInterceptors.set(interceptor);
    }

    public static ExecutorService wrapExecutorService(ExecutorService service) {
        return new ExecutorServiceWrapper(service, new Decorator());
    }

    public static class Decorator implements ExecutorServiceWrapper.Decorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            Collection<AsyncInvocationInterceptor> interceptors = threadBoundInterceptors.get();
            return () -> {
                if (interceptors != null) {
                    interceptors.forEach(AsyncInvocationInterceptor::applyContext);
                }
                runnable.run();
            };
        }

        @Override
        public <V> Callable<V> decorate(Callable<V> callable) {
            Collection<AsyncInvocationInterceptor> interceptors = threadBoundInterceptors.get();
            return () -> {
                if (interceptors != null) {
                    interceptors.forEach(AsyncInvocationInterceptor::applyContext);
                }
                return callable.call();
            };
        }
    }

    private AsyncInvocationInterceptorHandler() {
    }
}
