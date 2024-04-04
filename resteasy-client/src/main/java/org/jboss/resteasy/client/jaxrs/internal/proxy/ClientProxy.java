package org.jboss.resteasy.client.jaxrs.internal.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientProxy implements InvocationHandler {
    private Map<Method, MethodInvoker> methodMap;
    private Class<?> clazz;
    private final WebTarget target;
    private final ProxyConfig config;

    public ClientProxy(final Map<Method, MethodInvoker> methodMap, final WebTarget target, final ProxyConfig config) {
        super();
        this.methodMap = methodMap;
        this.target = target;
        this.config = config;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object invoke(Object o, Method method, Object[] args)
            throws Throwable {
        // equals and hashCode were added for cases where the proxy is added to
        // collections. The Spring transaction management, for example, adds
        // transactional Resources to a Collection, and it calls equals and
        // hashCode.

        MethodInvoker clientInvoker = methodMap.get(method);
        if (clientInvoker == null) {
            if (method.getName().equals("equals")) {
                return o == args[0];
            } else if (method.getName().equals("hashCode")) {
                return this.hashCode();
            } else if (method.getName().equals("toString") && (args == null || args.length == 0)) {
                return this.toString();
            } else if (method.getName().equals("as") && args.length == 1 && args[0] instanceof Class) {
                return ProxyBuilder.proxy((Class<?>) args[0], target, config);
            } else if (method.isDefault()) {
                // We need to use the privateLookupIn and lookup(), compared to publicLookup(), as the proxy is likely
                // in a different module. See the JavaDoc for MethodHandles.privateLookupIn() for details on how this
                // works and this allows more permissive rules.
                if (System.getSecurityManager() == null) {
                    return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup())
                            .unreflectSpecial(method, clazz)
                            .bindTo(o)
                            .invokeWithArguments(args);
                }
                try {
                    return AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                        try {
                            return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup())
                                    .unreflectSpecial(method, clazz)
                                    .bindTo(o)
                                    .invokeWithArguments(args);
                        } catch (Throwable e) {
                            if (e instanceof Error) {
                                throw (Error) e;
                            }
                            if (e instanceof RuntimeException) {
                                throw (RuntimeException) e;
                            }
                            if (e instanceof Exception) {
                                throw (Exception) e;
                            }
                            throw new RuntimeException(e);
                        }
                    });
                } catch (PrivilegedActionException e) {
                    final Throwable real = e.getCause();
                    if (real != null) {
                        throw real;
                    }
                    throw e;
                }
            }
        }

        if (clientInvoker == null) {
            throw new RuntimeException(Messages.MESSAGES.couldNotFindMethod(method));
        }
        return clientInvoker.invoke(args);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ClientProxy))
            return false;
        ClientProxy other = (ClientProxy) obj;
        if (other == this)
            return true;
        if (other.clazz != this.clazz)
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    public String toString() {
        return Messages.MESSAGES.resteasyClientProxyFor(clazz.getName());
    }
}
