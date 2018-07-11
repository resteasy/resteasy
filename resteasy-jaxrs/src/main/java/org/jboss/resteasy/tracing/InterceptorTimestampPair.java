package org.jboss.resteasy.tracing;

public class InterceptorTimestampPair<T> {

    private final T interceptor;
    private final long timestamp;

    public InterceptorTimestampPair(final T interceptor, final long timestamp) {
        this.interceptor = interceptor;
        this.timestamp = timestamp;
    }

    public T getInterceptor() {
        return interceptor;
    }

    public long getTimestamp() {
        return timestamp;
    }
}