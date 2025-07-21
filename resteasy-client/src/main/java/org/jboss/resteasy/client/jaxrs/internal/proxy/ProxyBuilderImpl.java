package org.jboss.resteasy.client.jaxrs.internal.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.util.IsHttpMethod;

public class ProxyBuilderImpl<T> extends ProxyBuilder<T> {
    private static final Class<?>[] cClassArgArray = { Class.class };

    private final Class<T> iface;

    private final WebTarget webTarget;

    private ClassLoader loader;

    private MediaType serverConsumes;

    private MediaType serverProduces;

    private static <T> ClientInvoker createClientInvoker(Class<T> clazz, Method method, ResteasyWebTarget base,
            ProxyConfig config) {
        Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
        if (httpMethods == null || httpMethods.size() != 1) {
            throw new RuntimeException(Messages.MESSAGES.mustUseExactlyOneHttpMethod(method.toString()));
        }
        ClientInvoker invoker = new ClientInvoker(base, clazz, method, config);
        invoker.setHttpMethod(httpMethods.iterator().next());
        return invoker;
    }

    public ProxyBuilderImpl(final Class<T> iface, final WebTarget webTarget) {
        this.loader = Thread.currentThread().getContextClassLoader();
        this.iface = iface;
        this.webTarget = webTarget;
    }

    public ProxyBuilderImpl<T> classloader(ClassLoader cl) {
        this.loader = cl;
        return this;
    }

    public ProxyBuilderImpl<T> defaultProduces(MediaType type) {
        this.serverProduces = type;
        return this;
    }

    public ProxyBuilderImpl<T> defaultConsumes(MediaType type) {
        this.serverConsumes = type;
        return this;
    }

    public ProxyBuilderImpl<T> defaultProduces(String type) {
        this.serverProduces = MediaType.valueOf(type);
        return this;
    }

    public ProxyBuilderImpl<T> defaultConsumes(String type) {
        this.serverConsumes = MediaType.valueOf(type);
        return this;
    }

    public T build() {
        return build(new ProxyConfig(loader, serverConsumes, serverProduces));
    }

    @SuppressWarnings("unchecked")
    public T build(final ProxyConfig config) {
        WebTarget base = webTarget;
        if (iface.isAnnotationPresent(Path.class)) {
            Path path = iface.getAnnotation(Path.class);
            if (!path.value().equals("") && !path.value().equals("/")) {
                base = base.path(path.value());
            }
        }
        HashMap<Method, MethodInvoker> methodMap = new HashMap<Method, MethodInvoker>();
        for (Method method : iface.getMethods()) {
            // ignore the as method to allow declaration in client interfaces
            if ("as".equals(method.getName()) && Arrays.equals(method.getParameterTypes(), cClassArgArray)) {
                continue;
            }
            MethodInvoker invoker;
            Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
            if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class)
                    && method.getReturnType().isInterface()) {
                invoker = new SubResourceInvoker((ResteasyWebTarget) base, method, config);
            } else if (httpMethods == null) {
                // ignore methods without http method annotations
                continue;
            } else if (base instanceof ClientInvokerFactory) {
                invoker = ((ClientInvokerFactory) base).createClientInvoker(iface, method, config);
            } else {
                invoker = createClientInvoker(iface, method, (ResteasyWebTarget) base, config);
            }
            methodMap.put(method, invoker);
        }

        Class<?>[] intfs = { iface, ResteasyClientProxy.class };

        ClientProxy clientProxy = new ClientProxy(methodMap, base, config);
        // this is done so that equals and hashCode work ok. Adding the proxy to a
        // Collection will cause equals and hashCode to be invoked. The Spring
        // infrastructure had some problems without this.
        clientProxy.setClazz(iface);

        ClassLoader cl = config.getLoader();
        try {
            cl.loadClass(iface.getName());
        } catch (Throwable t) {
            cl = new DelegateClassLoader(iface.getClassLoader(), cl);
        }

        return (T) Proxy.newProxyInstance(cl, intfs, clientProxy);
    }

    public class DelegateClassLoader extends SecureClassLoader {
        private final ClassLoader delegate;

        private final ClassLoader parent;

        public DelegateClassLoader(final ClassLoader delegate, final ClassLoader parent) {
            super(parent);
            this.delegate = delegate;
            this.parent = parent;
        }

        /** {@inheritDoc} */
        @Override
        public Class<?> loadClass(final String className) throws ClassNotFoundException {
            if (parent != null) {
                try {
                    return parent.loadClass(className);
                } catch (ClassNotFoundException cnfe) {
                    //NOOP, use delegate
                }
            }
            return delegate.loadClass(className);
        }

        /** {@inheritDoc} */
        @Override
        public URL getResource(final String name) {
            URL url = null;
            if (parent != null) {
                url = parent.getResource(name);
            }
            return (url == null) ? delegate.getResource(name) : url;
        }

        /** {@inheritDoc} */
        @Override
        public Enumeration<URL> getResources(final String name) throws IOException {
            final ArrayList<Enumeration<URL>> foundResources = new ArrayList<Enumeration<URL>>();

            foundResources.add(delegate.getResources(name));
            if (parent != null) {
                foundResources.add(parent.getResources(name));
            }

            return new Enumeration<URL>() {
                private int position = foundResources.size() - 1;

                public boolean hasMoreElements() {
                    while (position >= 0) {
                        if (foundResources.get(position).hasMoreElements()) {
                            return true;
                        }
                        position--;
                    }
                    return false;
                }

                public URL nextElement() {
                    while (position >= 0) {
                        try {
                            return (foundResources.get(position)).nextElement();
                        } catch (NoSuchElementException e) {
                        }
                        position--;
                    }
                    throw new NoSuchElementException();
                }
            };
        }

        /** {@inheritDoc} */
        @Override
        public InputStream getResourceAsStream(final String name) {
            InputStream is = null;
            if (parent != null) {
                is = parent.getResourceAsStream(name);
            }
            return (is == null) ? delegate.getResourceAsStream(name) : is;
        }
    }

}
