package org.jboss.resteasy.microprofile.client;

import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.interceptor.InvocationContext;
import jakarta.ws.rs.client.ResponseProcessingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

public class InvocationContextImpl implements InvocationContext {

    private final Object target;

    private final Method method;

    private Object[] args;

    private final int position;

    private final Map<String, Object> contextData;

    private final List<InterceptorInvocation> chain;

    /**
     * @param target
     * @param method
     * @param args
     * @param chain
     */
    public InvocationContextImpl(final Object target, final Method method, final Object[] args, final List<InterceptorInvocation> chain) {
        this(target, method, args, chain, 0);
    }

    private InvocationContextImpl(final Object target, final Method method, final Object[] args, final List<InterceptorInvocation> chain, final int position) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.contextData = new HashMap<>();
        this.position = position;
        this.chain = chain;
    }

    boolean hasNextInterceptor() {
        return position < chain.size();
    }

    protected Object invokeNext() throws Exception {
        return chain.get(position).invoke(nextContext());
    }

    private InvocationContext nextContext() {
        return new InvocationContextImpl(target, method, args, chain, position + 1);
    }

    protected Object interceptorChainCompleted() throws Exception {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CompletionException) {
                cause = cause.getCause();
            }
            if (cause instanceof ExceptionMapping.HandlerException) {
                ((ExceptionMapping.HandlerException)cause).mapException(method);
            }
            if (cause instanceof ResponseProcessingException) {
                ResponseProcessingException rpe = (ResponseProcessingException) cause;
                // Note that the default client engine leverages a single connection
                // MP FT: we need to close the response otherwise we would not be able to retry if the method returns jakarta.ws.rs.core.Response
                rpe.getResponse().close();
                cause = rpe.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            }
            throw e;
        }
    }

    @Override
    public Object proceed() throws Exception {
        try {
            if (hasNextInterceptor()) {
                return invokeNext();
            } else {
                return interceptorChainCompleted();
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw new RuntimeException(cause);
        }
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Constructor<?> getConstructor() {
        return null;
    }

    @Override
    public Object[] getParameters() throws IllegalStateException {
        return args;
    }

    @Override
    public void setParameters(Object[] params) throws IllegalStateException, IllegalArgumentException {
        this.args = params;
    }

    @Override
    public Map<String, Object> getContextData() {
        return contextData;
    }

    @Override
    public Object getTimer() {
        return null;
    }

    public static class InterceptorInvocation {

        @SuppressWarnings("rawtypes")
        private final Interceptor interceptor;

        private final Object interceptorInstance;

        public InterceptorInvocation(final Interceptor<?> interceptor, final Object interceptorInstance) {
            this.interceptor = interceptor;
            this.interceptorInstance = interceptorInstance;
        }

        @SuppressWarnings("unchecked")
        Object invoke(InvocationContext ctx) throws Exception {
            return interceptor.intercept(InterceptionType.AROUND_INVOKE, interceptorInstance, ctx);
        }
    }

}
