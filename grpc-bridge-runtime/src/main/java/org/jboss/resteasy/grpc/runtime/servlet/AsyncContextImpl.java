package org.jboss.resteasy.grpc.runtime.servlet;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.jboss.resteasy.concurrent.ContextualExecutors;

public class AsyncContextImpl implements AsyncContext {

    private static ExecutorService executorService = ContextualExecutors.threadPool();

    private ServletRequest servletRequest;
    private ServletResponse servletResponse;
    private Set<AsyncListener> listeners = new HashSet<AsyncListener>();
    long timeout;
    private volatile boolean complete = false;

    public AsyncContextImpl(final ServletRequest servletRequest, final ServletResponse servletResponse) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public ServletRequest getRequest() {
        return servletRequest;
    }

    @Override
    public ServletResponse getResponse() {
        return servletResponse;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        throw new RuntimeException("hasOriginalRequestAndResponse() not implemented");
    }

    @Override
    public void dispatch() {
        throw new RuntimeException("dispatch() not implemented");
    }

    @Override
    public void dispatch(String path) {
        throw new RuntimeException("dispatch() not implemented");
    }

    @Override
    public void dispatch(ServletContext context, String path) {
        throw new RuntimeException("dispatch() not implemented");
    }

    @Override
    public synchronized void complete() {
        if (complete) {
            return;
        }
        complete = true;
    }

    @Override
    public void start(Runnable run) {
        executorService.execute(run);
    }

    @Override
    public void addListener(AsyncListener listener) {
        listeners.add(listener);
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
        throw new RuntimeException("addListener() not implemented");
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        throw new RuntimeException("createListener() not implemented");
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }
}
