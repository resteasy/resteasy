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

package dev.resteasy.embedded.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServers;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.DelegateResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.ContainerInstance;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.servlet.Container;
import org.jboss.weld.environment.servlet.Listener;
import org.jboss.weld.environment.undertow.UndertowContainer;

import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class CdiResteasyDeployment extends DelegateResteasyDeployment implements ResteasyDeployment {
    private static final AtomicLong COUNTER = new AtomicLong();

    private final Lock lock = new ReentrantLock();
    private final String containerName;
    private final SeBootstrap.Configuration configuration;
    private ResteasyDeploymentImpl delegate;
    private Weld weld;
    private DeploymentInfo deploymentInfo;
    private WeldContainer container;
    private boolean started = false;

    CdiResteasyDeployment(final SeBootstrap.Configuration configuration) {
        super(null);
        delegate = new ResteasyDeploymentImpl();
        this.containerName = String.format("resteasy-undertow-cdi-%d", COUNTER.incrementAndGet());
        this.configuration = configuration;
    }

    @Override
    public void start() {
        lock.lock();
        try {
            if (started) {
                return;
            }
            deploymentInfo = createDeploymentInfo();
            super.setInjectorFactory(new CdiInjectorFactory(getContainer().getBeanManager()));
            super.start();
            started = true;
        } catch (Exception e) {
            try {
                stop();
            } catch (Exception ex) {
                e.addSuppressed(ex);
            }
            throw e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            if (!started) {
                return;
            }
            try {
                super.stop();
            } finally {
                if (weld != null) {
                    weld.shutdown();
                    weld = null;
                }
            }
        } finally {
            started = false;
            deploymentInfo = null;
            delegate = newDelegate(delegate);
            lock.unlock();
        }
    }

    @Override
    protected ResteasyDeployment getDelegate() {
        lock.lock();
        try {
            return delegate;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "CdiResteasyDeployment{" + containerName + "}";
    }

    DeploymentInfo deploymentInfo() {
        lock.lock();
        try {
            return deploymentInfo;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private ContainerInstance getContainer() {
        if (weld == null) {
            weld = new Weld(containerName)
                    // Register these as bean defining annotations
                    .addBeanDefiningAnnotations(Path.class, Provider.class, ApplicationPath.class)
                    // Do not register the shutdown hook as stopping the server may execute in a separate shutdown hook.
                    .skipShutdownHook();
        }
        if (container == null || !container.isRunning()) {
            container = weld.initialize();
        }
        return container;
    }

    private DeploymentInfo createDeploymentInfo() {
        DeploymentInfo deploymentInfo;
        if (configuration.hasProperty(UndertowConfigurationOptions.DEPLOYMENT_INFO)) {
            final Object deployment = configuration.property(UndertowConfigurationOptions.DEPLOYMENT_INFO);
            if (deployment instanceof DeploymentInfo) {
                deploymentInfo = ((DeploymentInfo) deployment);
            } else {
                LogMessages.LOGGER.invalidProperty(UndertowConfigurationOptions.DEPLOYMENT_INFO,
                        deployment.getClass().getName(), DeploymentInfo.class.getName());
                deploymentInfo = new DeploymentInfo();
            }
        } else {
            deploymentInfo = new DeploymentInfo();
        }
        return configure(deploymentInfo, configuration);
    }

    @SuppressWarnings("unchecked")
    private DeploymentInfo configure(final DeploymentInfo deploymentInfo, final SeBootstrap.Configuration configuration) {
        final ContainerInstance container = getContainer();

        // Determine the servlet mapping name
        String mapping = EmbeddedServers.checkContextPath(this);
        if (!mapping.endsWith("/")) {
            mapping += "/";
        }
        mapping = mapping + "*";

        // Configure the RESTEasy Servlet
        final ServletInfo resteasyServlet;
        if (deploymentInfo.getServlets().containsKey("ResteasyServlet")) {
            resteasyServlet = deploymentInfo.getServlets().get("ResteasyServlet");
        } else {
            resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
                    .setAsyncSupported(true)
                    .setLoadOnStartup(1)
                    .addMapping(mapping);
        }
        final Map<String, String> servletConfigInitParams = new HashMap<>();
        final Map<String, String> servletContextInitParams = new HashMap<>();

        if (!"/*".equals(mapping)) {
            // Configure the mapping prefix for RESTEasy
            final String prefix = mapping.substring(0, mapping.length() - 2);
            servletConfigInitParams.put("resteasy.servlet.mapping.prefix", prefix);
        }

        // Check for context parameters
        if (configuration.hasProperty(UndertowConfigurationOptions.CONTEXT_PARAMETERS)) {
            final Object value = configuration.property(UndertowConfigurationOptions.CONTEXT_PARAMETERS);
            if (value instanceof Map) {
                servletContextInitParams.putAll(((Map<String, String>) value));
            } else {
                LogMessages.LOGGER.invalidProperty(UndertowConfigurationOptions.CONTEXT_PARAMETERS, value.getClass()
                        .getName(), Map.class.getName());
            }
        }
        // Ensure the Undertow Weld Container is always the one chosen.
        servletContextInitParams.put(Container.CONTEXT_PARAM_CONTAINER_CLASS, UndertowContainer.class.getName());

        // Process the init parameters and register the ResteasyConfiguration
        servletConfigInitParams.forEach(resteasyServlet::addInitParam);
        servletContextInitParams.forEach(deploymentInfo::addInitParameter);
        final UndertowResteasyConfiguration resteasyConfiguration = new UndertowResteasyConfiguration(servletConfigInitParams,
                servletContextInitParams, configuration);
        getDefaultContextObjects().put(ResteasyConfiguration.class, resteasyConfiguration);

        // Determine the context path
        final String contextPath = EmbeddedServers.checkContextPath(configuration.rootPath());
        final Optional<Class<? extends Application>> applicationClass;
        try {
            applicationClass = EmbeddedServers.resolveApplication(this);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw LogMessages.LOGGER.failedToResolveApplicationClass(e, this);
        }

        if (deploymentInfo.getDefaultMultipartConfig() == null) {
            applicationClass.map(c -> c.getAnnotation(MultipartConfig.class))
                    .ifPresent(multipartConfig -> deploymentInfo
                            .setDefaultMultipartConfig(new MultipartConfigElement(multipartConfig)));
        }
        return deploymentInfo
                // Set up deployment specific info
                .setClassLoader(applicationClass.map(Class::getClassLoader).orElse(getClass().getClassLoader()))
                .setContextPath(contextPath)
                .setDeploymentName(containerName)
                // Set up the RESTEasy Servlet
                .addServletContextAttribute(ResteasyDeployment.class.getName(), this)
                // Add the bean manager
                .addServletContextAttribute(BeanManager.class.getName(), container.getBeanManager())
                .addServlet(resteasyServlet)
                // Configure the Weld listener
                .addListener(Servlets.listener(Listener.class, new ImmediateInstanceFactory<>(Listener.using(container))));
    }

    private static ResteasyDeploymentImpl newDelegate(final ResteasyDeployment old) {
        final ResteasyDeploymentImpl deployment = new ResteasyDeploymentImpl();
        deployment.merge(old);
        deployment.setApplication(old.getApplication());
        deployment.setApplicationClass(old.getApplicationClass());
        return deployment;
    }
}
