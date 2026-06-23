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

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.spi.DelegateResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.ContainerInstance;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class CdiResteasyDeployment extends DelegateResteasyDeployment implements ResteasyDeployment {
    private Weld weld;
    private final Object lock = new Object();
    private final String containerName;
    private WeldContainer container;
    private ResteasyDeployment delegate;

    CdiResteasyDeployment(final String containerName) {
        super(null);
        delegate = new ResteasyDeploymentImpl();
        this.containerName = containerName;
    }

    @Override
    public void start() {
        synchronized (lock) {
            super.setInjectorFactory(new CdiInjectorFactory(getContainer().getBeanManager()));
            super.start();
        }
    }

    @Override
    public void stop() {
        synchronized (lock) {
            final ResteasyDeployment newDelegate = newDelegate(delegate);
            delegate.stop();
            delegate = newDelegate;
            if (weld != null) {
                weld.shutdown();
                weld = null;
            }
        }
    }

    @Override
    protected ResteasyDeployment getDelegate() {
        synchronized (lock) {
            return delegate;
        }
    }

    @SuppressWarnings("unchecked")
    ContainerInstance getContainer() {
        synchronized (lock) {
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
        }
        return container;
    }

    String getDeploymentName() {
        return containerName;
    }

    private static ResteasyDeployment newDelegate(final ResteasyDeployment old) {
        final ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.merge(old);
        deployment.setApplication(old.getApplication());
        deployment.setApplicationClass(old.getApplicationClass());
        return deployment;
    }
}
