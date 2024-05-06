package org.jboss.resteasy.plugins.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.config.ConfigurationFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegisterBuiltin {
    private static final Map<ClassLoader, ResteasyProviderFactory> configuredClientFactories = new WeakHashMap<>();
    private static final boolean gzipForCachedFactories = isGZipEnabled();

    public static synchronized ResteasyProviderFactory getClientInitializedResteasyProviderFactory(ClassLoader cl) {
        ResteasyProviderFactory rpf = null;
        final boolean gzip = isGZipEnabled();
        if (gzipForCachedFactories == gzip) {
            rpf = configuredClientFactories.get(cl);
        }
        if (rpf == null) {
            rpf = new ResteasyProviderFactoryImpl(RuntimeType.CLIENT) {
                @Override
                public RuntimeType getRuntimeType() {
                    return RuntimeType.CLIENT;
                }
            };
            if (!rpf.isBuiltinsRegistered()) {
                register(rpf);
            }
            if (gzipForCachedFactories == gzip) {
                configuredClientFactories.put(cl, rpf);
            }
        }
        return rpf;
    }

    public static void register(ResteasyProviderFactory factory) {
        register(factory, Set.of());
    }

    public static void register(ResteasyProviderFactory factory, Set<String> disabledProviders) {
        final ResteasyProviderFactory monitor = (factory instanceof ThreadLocalResteasyProviderFactory)
                ? ((ThreadLocalResteasyProviderFactory) factory).getDelegate()
                : factory;
        synchronized (monitor) {
            if (factory.isBuiltinsRegistered() || !factory.isRegisterBuiltins())
                return;
            try {
                registerProviders(factory, disabledProviders);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            factory.setBuiltinsRegistered(true);
        }
    }

    public static void registerProviders(ResteasyProviderFactory factory) throws Exception {
        registerProviders(factory, Set.of());
    }

    public static void registerProviders(ResteasyProviderFactory factory, Set<String> disabledProviders) throws Exception {
        Map<String, URL> origins = scanBuiltins(disabledProviders);
        for (final Entry<String, URL> entry : origins.entrySet()) {
            final String line = entry.getKey();
            try {
                Class<?> clazz;
                if (System.getSecurityManager() == null) {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(line);
                } else {
                    clazz = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                        @Override
                        public Class<?> run() throws ClassNotFoundException {
                            return Thread.currentThread().getContextClassLoader().loadClass(line);
                        }
                    });
                }

                factory.registerProvider(clazz, true);
            } catch (NoClassDefFoundError e) {
                LogMessages.LOGGER.noClassDefFoundErrorError(line, entry.getValue(), e);
            } catch (ClassNotFoundException | PrivilegedActionException ex) {
                LogMessages.LOGGER.classNotFoundException(line, entry.getValue(), ex);
            }
        }
        if (isGZipEnabled()) {
            factory.registerProvider(GZIPDecodingInterceptor.class, true);
            factory.registerProvider(GZIPEncodingInterceptor.class, true);
        }
    }

    public static Map<String, URL> scanBuiltins() throws IOException, PrivilegedActionException {
        return scanBuiltins(Set.of());
    }

    public static Map<String, URL> scanBuiltins(final Set<String> disabledProviders)
            throws IOException, PrivilegedActionException {
        Enumeration<URL> en;
        if (System.getSecurityManager() == null) {
            en = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/" + Providers.class.getName());
        } else {
            en = AccessController.doPrivileged(new PrivilegedExceptionAction<Enumeration<URL>>() {
                @Override
                public Enumeration<URL> run() throws IOException {
                    return Thread.currentThread().getContextClassLoader()
                            .getResources("META-INF/services/" + Providers.class.getName());
                }
            });
        }

        Map<String, URL> origins = new HashMap<String, URL>();
        while (en.hasMoreElements()) {
            final URL url = en.nextElement();
            InputStream is;
            if (System.getSecurityManager() == null) {
                is = url.openStream();
            } else {
                is = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                    @Override
                    public InputStream run() throws IOException {
                        return url.openStream();
                    }
                });
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    int commentIdx = line.indexOf('#');
                    if (commentIdx >= 0) {
                        line = line.substring(0, commentIdx);
                    }
                    line = line.trim();
                    if (line.isEmpty())
                        continue;
                    if (disabledProviders.contains(line)) {
                        LogMessages.LOGGER.debugf("Skipping provider \"%s\" as it is marked as disabled.", line);
                        continue;
                    }
                    origins.put(line, url);
                }
            } finally {
                is.close();
            }
        }
        return origins;
    }

    public static boolean isGZipEnabled() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String value = ConfigurationFactory.getInstance().getConfiguration()
                        .getOptionalValue("resteasy.allowGzip", String.class).orElse(null);
                if ("".equals(value))
                    return Boolean.FALSE;
                return Boolean.parseBoolean(value);
            }
        });
    }

}
