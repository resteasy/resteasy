package org.jboss.resteasy.microprofile.client.utils;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestContextImpl;

import jakarta.ws.rs.client.ClientRequestContext;

import java.lang.reflect.Method;

/**
 * A utility class to pull out common operations on {@link ClientRequestContext}
 */
public class ClientRequestContextUtils {

    /**
     * Get {@link Method} for the client call from {@link ClientRequestContext}
     * @param requestContext the context
     * @return the method
     */
    public static Method getMethod(ClientRequestContext requestContext) {
        if(requestContext instanceof ClientRequestContextImpl == false) {
            throw new RuntimeException("Failed to get ClientInvocation from request context. Is RestEasy client used underneath?");
        }
        ClientInvocation invocation = ((ClientRequestContextImpl)requestContext).getInvocation();
        return invocation.getClientInvoker().getMethod();
    }

    /**
     * Get {@link Class} for the client call from {@link ClientRequestContext}
     * @param requestContext the context
     * @return the class
     */
    public static Class<?> getDeclaringClass(ClientRequestContext requestContext) {
        if(requestContext instanceof ClientRequestContextImpl == false) {
            throw new RuntimeException("Failed to get ClientInvocation from request context. Is RestEasy client used underneath?");
        }
        ClientInvocation invocation = ((ClientRequestContextImpl)requestContext).getInvocation();
        return invocation.getClientInvoker().getDeclaring();
    }

    private ClientRequestContextUtils() {
    }
}
