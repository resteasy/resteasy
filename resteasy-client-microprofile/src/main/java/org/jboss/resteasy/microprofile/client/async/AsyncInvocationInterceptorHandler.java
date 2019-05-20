/**
 * Copyright 2015-2017 Red Hat, Inc, and individual contributors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.microprofile.client.async;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptor;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class AsyncInvocationInterceptorHandler {

    private static final ThreadLocal<Collection<AsyncInvocationInterceptor>> threadBoundInterceptors = new ThreadLocal<>();

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
            threadBoundInterceptors.remove();
            return () -> {
                if (interceptors != null) {
                    interceptors.forEach(AsyncInvocationInterceptor::applyContext);
                }
                try {
                    runnable.run();
                } finally {
                    interceptors.forEach(AsyncInvocationInterceptor::removeContext);
                }
            };
        }

        @Override
        public <V> Callable<V> decorate(Callable<V> callable) {
            Collection<AsyncInvocationInterceptor> interceptors = threadBoundInterceptors.get();
            threadBoundInterceptors.remove();
            return () -> {
                if (interceptors != null) {
                    interceptors.forEach(AsyncInvocationInterceptor::applyContext);
                }
                try {
                    return callable.call();
                } finally {
                    interceptors.forEach(AsyncInvocationInterceptor::removeContext);
                }
            };
        }
    }

    private AsyncInvocationInterceptorHandler() {
    }
}
