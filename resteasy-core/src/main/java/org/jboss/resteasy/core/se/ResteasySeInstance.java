/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.core.se;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.SeBootstrap.Instance;
import jakarta.ws.rs.core.Application;

import org.jboss.jandex.Index;
import org.jboss.logging.Logger;
import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.scanner.ResourceScanner;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServers;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * An implementation of a {@link Instance}.
 *
 * @author <a href="mailto:jperkins@redha.t.com">James R. Perkins</a>
 * @since 6.1
 */
public class ResteasySeInstance implements Instance {
    private static final Logger LOGGER = Logger.getLogger(ResteasySeInstance.class);
    private final EmbeddedServer server;
    private final Configuration configuration;
    private final ExecutorService executor;

    private ResteasySeInstance(final EmbeddedServer server, final Configuration configuration,
            final ExecutorService executor) {
        this.server = server;
        this.configuration = configuration;
        this.executor = executor;
    }

    /**
     * Creates a new {@link Instance} based on the {@linkplain Application application} and
     * {@linkplain Configuration configuration} passed in.
     * <p>
     * Note that if your {@link Application} does not override the {@link Application#getClasses()} or
     * {@link Application#getSingletons()} a {@linkplain Index Jandex index} is used to find resources and providers.
     * It's suggested that your application has a {@code META-INF/jandex.idx} or you provide an index with the
     * {@link ConfigurationOption#JANDEX_INDEX} configuration option. If neither of those exist, the class path itself
     * is indexed which could have significant performance impacts.
     * </p>
     *
     * @param application   the application to use for this instance
     * @param configuration the configuration used to configure the instance
     *
     * @return a {@link CompletionStage} which asynchronously produces and {@link Instance}
     */
    public static CompletionStage<Instance> create(final Application application,
            final Configuration configuration) {
        final ExecutorService executor = ContextualExecutors.threadPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                final Class<?> applicationClass = application.getClass();
                String applicationPath = null;
                if (applicationClass.isAnnotationPresent(ApplicationPath.class)) {
                    applicationPath = applicationClass.getAnnotation(ApplicationPath.class).value();
                }
                final Configuration config = ResteasySeConfiguration.from(configuration, applicationPath);
                final EmbeddedServer server = EmbeddedServers.findServer(config);
                final ResteasyDeployment deployment = server.getDeployment();
                deployment.setRegisterBuiltin(ConfigurationOption.REGISTER_BUILT_INS.getValue(config));
                deployment.setApplication(application);
                try {
                    scanForResources(deployment, application, config);
                } catch (IOException e) {
                    throw Messages.MESSAGES.failedToScanResources(e);
                }
                deployment.start();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debugf("Application %s used for %s", deployment.getApplication(), server);
                    deployment.getResourceClasses()
                            .forEach(name -> LOGGER.debugf("Resource %s found for %s", name, server));
                    deployment.getProviderClasses()
                            .forEach(name -> LOGGER.debugf("Provider %s found for %s", name, server));
                }
                server.start(config);
                return new ResteasySeInstance(server, config, executor);
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        }, executor);
    }

    /**
     * Creates a new {@link Instance} based on the {@linkplain Application application} and
     * {@linkplain Configuration configuration} passed in.
     * <p>
     * Note that if your {@link Application} does not override the {@link Application#getClasses()} or
     * {@link Application#getSingletons()} a {@linkplain Index Jandex index} is used to find resources and providers.
     * It's suggested that your application has a {@code META-INF/jandex.idx} or you provide an index with the
     * {@link ConfigurationOption#JANDEX_INDEX} configuration option. If neither of those exist, the class path itself
     * is indexed which could have significant performance impacts.
     * </p>
     *
     * @param applicationClass the application to use for this instance
     * @param configuration    the configuration used to configure the instance
     *
     * @return a {@link CompletionStage} which asynchronously produces and {@link Instance}
     */
    public static CompletionStage<Instance> create(final Class<? extends Application> applicationClass,
            final Configuration configuration) {
        final ExecutorService executor = ContextualExecutors.threadPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                String applicationPath = null;
                if (applicationClass.isAnnotationPresent(ApplicationPath.class)) {
                    applicationPath = applicationClass.getAnnotation(ApplicationPath.class).value();
                }
                final Configuration config = ResteasySeConfiguration.from(configuration, applicationPath);
                final EmbeddedServer server = EmbeddedServers.findServer(config);
                final ResteasyDeployment deployment = server.getDeployment();
                deployment.setRegisterBuiltin(ConfigurationOption.REGISTER_BUILT_INS.getValue(config));
                // First we need to check how the application was passed
                deployment.setApplicationClass(applicationClass.getName());
                deployment.start();
                final Application application = deployment.getApplication();
                try {
                    scanForResources(deployment, application, config);
                    // We need to re-run the registration of resources
                    deployment.registration();
                } catch (IOException e) {
                    throw Messages.MESSAGES.failedToScanResources(e);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debugf("Application %s used for %s", deployment.getApplication(), server);
                    deployment.getResourceClasses()
                            .forEach(name -> LOGGER.debugf("Resource %s found for %s", name, server));
                    deployment.getProviderClasses()
                            .forEach(name -> LOGGER.debugf("Provider %s found for %s", name, server));
                }
                server.start(config);
                return new ResteasySeInstance(server, config, executor);
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        }, executor);
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public CompletionStage<StopResult> stop() {
        final CompletableFuture<StopResult> cf = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                server.stop();
                cf.complete(new StopResult() {
                    @Override
                    public <T> T unwrap(final Class<T> nativeClass) {
                        if (nativeClass != null && nativeClass.isInstance(server)) {
                            return nativeClass.cast(server);
                        }
                        return null;
                    }
                });
            } catch (Throwable t) {
                cf.completeExceptionally(t);
            }
        });
        return cf.whenComplete(((stopResult, throwable) -> executor.shutdownNow()));
    }

    @Override
    public <T> T unwrap(final Class<T> nativeClass) {
        if (nativeClass != null && nativeClass.isInstance(server)) {
            return nativeClass.cast(server);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static void scanForResources(final ResteasyDeployment deployment, final Application application,
            final Configuration configuration)
            throws IOException {
        // If the application is not null we may not need to scan
        if ((application.getClasses() != null && !application.getClasses().isEmpty()) ||
                (application.getSingletons() != null && !application.getSingletons().isEmpty())) {
            return;
        }
        // Scan the class path for applications and resources
        final Index index = ConfigurationOption.JANDEX_INDEX.getValue(configuration);
        final ResourceScanner resourceScanner;
        if (index == null) {
            resourceScanner = ResourceScanner.fromClassPath(classLoader(application.getClass()),
                    ConfigurationOption.JANDEX_CLASS_PATH_FILTER.getValue(configuration));
        } else {
            resourceScanner = ResourceScanner.of(index);
        }
        // If the jakarta.ws.rs.loadServices is set to false we should not load any resources
        if (loadServices(application.getProperties())) {
            final Set<String> resources = resourceScanner.getResources().stream()
                    // This may end up being discovered, but we don't want to register it this way.
                    .filter(name -> !AsynchronousDispatcher.class.getName().equals(name))
                    .collect(Collectors.toSet());
            deployment.getScannedResourceClasses().addAll(resources);
            deployment.getScannedProviderClasses().addAll(resourceScanner.getProviders());
        }
    }

    private static ClassLoader classLoader() {
        if (System.getSecurityManager() == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = ResteasySeInstance.class.getClassLoader();
            }
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            return cl;
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = ResteasySeInstance.class.getClassLoader();
            }
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            return cl;
        });
    }

    private static ClassLoader classLoader(final Class<?> c) {
        if (c == null) {
            return classLoader();
        }
        if (System.getSecurityManager() == null) {
            return c.getClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) c::getClassLoader);
    }

    private static boolean loadServices(final Map<String, Object> props) {
        final Object value = props.get("jakarta.ws.rs.loadServices");
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return true;
    }
}
