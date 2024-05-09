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

package org.jboss.resteasy.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.core.Application;

/**
 * A simple delegating {@link ResteasyDeployment}.
 * <p>
 * All the delegate methods uses {@link #getDelegate()} to determine the delegate. This allows sub-classes to override
 * the method without requiring the delegating to be known at construction time.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("rawtypes")
public class DelegateResteasyDeployment implements ResteasyDeployment {
    private final ResteasyDeployment delegate;

    /**
     * Creates a new delegating deployment.
     * <p>
     * If a {@code null} delegate is passed the {@link #getDelegate()} must be overridden.
     * </p>
     *
     * @param delegate the delegate or {@code null} if {@link #getDelegate()} is overridden
     */
    public DelegateResteasyDeployment(final ResteasyDeployment delegate) {
        this.delegate = delegate;
    }

    @Override
    public void start() {
        getDelegate().start();
    }

    @Override
    public void merge(final ResteasyDeployment other) {
        getDelegate().merge(other);
    }

    @Override
    public void registration() {
        getDelegate().registration();
    }

    @Override
    public void stop() {
        getDelegate().stop();
    }

    @Override
    public boolean isUseContainerFormParams() {
        return getDelegate().isUseContainerFormParams();
    }

    @Override
    public void setUseContainerFormParams(final boolean useContainerFormParams) {
        getDelegate().setUseContainerFormParams(useContainerFormParams);
    }

    @Override
    public List<String> getJndiComponentResources() {
        return getDelegate().getJndiComponentResources();
    }

    @Override
    public void setJndiComponentResources(final List<String> jndiComponentResources) {
        getDelegate().setJndiComponentResources(jndiComponentResources);
    }

    @Override
    public String getApplicationClass() {
        return getDelegate().getApplicationClass();
    }

    @Override
    public void setApplicationClass(final String applicationClass) {
        getDelegate().setApplicationClass(applicationClass);
    }

    @Override
    public String getInjectorFactoryClass() {
        return getDelegate().getInjectorFactoryClass();
    }

    @Override
    public void setInjectorFactoryClass(final String injectorFactoryClass) {
        getDelegate().setInjectorFactoryClass(injectorFactoryClass);
    }

    @Override
    public boolean isDeploymentSensitiveFactoryEnabled() {
        return getDelegate().isDeploymentSensitiveFactoryEnabled();
    }

    @Override
    public void setDeploymentSensitiveFactoryEnabled(final boolean deploymentSensitiveFactoryEnabled) {
        getDelegate().setDeploymentSensitiveFactoryEnabled(deploymentSensitiveFactoryEnabled);
    }

    @Override
    public boolean isAsyncJobServiceEnabled() {
        return getDelegate().isAsyncJobServiceEnabled();
    }

    @Override
    public void setAsyncJobServiceEnabled(final boolean asyncJobServiceEnabled) {
        getDelegate().setAsyncJobServiceEnabled(asyncJobServiceEnabled);
    }

    @Override
    public int getAsyncJobServiceMaxJobResults() {
        return getDelegate().getAsyncJobServiceMaxJobResults();
    }

    @Override
    public void setAsyncJobServiceMaxJobResults(final int asyncJobServiceMaxJobResults) {
        getDelegate().setAsyncJobServiceMaxJobResults(asyncJobServiceMaxJobResults);
    }

    @Override
    public long getAsyncJobServiceMaxWait() {
        return getDelegate().getAsyncJobServiceMaxWait();
    }

    @Override
    public void setAsyncJobServiceMaxWait(final long asyncJobServiceMaxWait) {
        getDelegate().setAsyncJobServiceMaxWait(asyncJobServiceMaxWait);
    }

    @Override
    public int getAsyncJobServiceThreadPoolSize() {
        return getDelegate().getAsyncJobServiceThreadPoolSize();
    }

    @Override
    public void setAsyncJobServiceThreadPoolSize(final int asyncJobServiceThreadPoolSize) {
        getDelegate().setAsyncJobServiceThreadPoolSize(asyncJobServiceThreadPoolSize);
    }

    @Override
    public String getAsyncJobServiceBasePath() {
        return getDelegate().getAsyncJobServiceBasePath();
    }

    @Override
    public void setAsyncJobServiceBasePath(final String asyncJobServiceBasePath) {
        getDelegate().setAsyncJobServiceBasePath(asyncJobServiceBasePath);
    }

    @Override
    public Application getApplication() {
        return getDelegate().getApplication();
    }

    @Override
    public void setApplication(final Application application) {
        getDelegate().setApplication(application);
    }

    @Override
    public boolean isRegisterBuiltin() {
        return getDelegate().isRegisterBuiltin();
    }

    @Override
    public void setRegisterBuiltin(final boolean registerBuiltin) {
        getDelegate().setRegisterBuiltin(registerBuiltin);
    }

    @Override
    public List<String> getProviderClasses() {
        return getDelegate().getProviderClasses();
    }

    @Override
    public void setProviderClasses(final List<String> providerClasses) {
        getDelegate().setProviderClasses(providerClasses);
    }

    @Override
    public List<Object> getProviders() {
        return getDelegate().getProviders();
    }

    @Override
    public void setProviders(final List<Object> providers) {
        getDelegate().setProviders(providers);
    }

    @Override
    public Set<String> getDisabledProviderClasses() {
        return getDelegate().getDisabledProviderClasses();
    }

    @Override
    public void addDisabledProviderClass(final String disabledProviderClass) {
        getDelegate().addDisabledProviderClass(disabledProviderClass);
    }

    @Override
    public void addDisabledProviderClasses(final Set<String> disabledProviderClasses) {
        getDelegate().addDisabledProviderClasses(disabledProviderClasses);
    }

    @Override
    public void setDisabledProviderClasses(final Set<String> disabledProviderClasses) {
        getDelegate().setDisabledProviderClasses(disabledProviderClasses);
    }

    @Override
    public void setDisabledProviderClasses(final String... disabledProviderClasses) {
        getDelegate().setDisabledProviderClasses(disabledProviderClasses);
    }

    @Override
    public List<Class> getActualProviderClasses() {
        return getDelegate().getActualProviderClasses();
    }

    @Override
    public void setActualProviderClasses(final List<Class> actualProviderClasses) {
        getDelegate().setActualProviderClasses(actualProviderClasses);
    }

    @Override
    public List<Class> getActualResourceClasses() {
        return getDelegate().getActualResourceClasses();
    }

    @Override
    public void setActualResourceClasses(final List<Class> actualResourceClasses) {
        getDelegate().setActualResourceClasses(actualResourceClasses);
    }

    @Override
    public boolean isSecurityEnabled() {
        return getDelegate().isSecurityEnabled();
    }

    @Override
    public void setSecurityEnabled(final boolean securityEnabled) {
        getDelegate().setSecurityEnabled(securityEnabled);
    }

    @Override
    public List<String> getJndiResources() {
        return getDelegate().getJndiResources();
    }

    @Override
    public void setJndiResources(final List<String> jndiResources) {
        getDelegate().setJndiResources(jndiResources);
    }

    @Override
    public List<String> getResourceClasses() {
        return getDelegate().getResourceClasses();
    }

    @Override
    public void setResourceClasses(final List<String> resourceClasses) {
        getDelegate().setResourceClasses(resourceClasses);
    }

    @Override
    public Map<String, String> getMediaTypeMappings() {
        return getDelegate().getMediaTypeMappings();
    }

    @Override
    public void setMediaTypeMappings(final Map<String, String> mediaTypeMappings) {
        getDelegate().setMediaTypeMappings(mediaTypeMappings);
    }

    @Override
    public List<Object> getResources() {
        return getDelegate().getResources();
    }

    @Override
    public void setResources(final List<Object> resources) {
        getDelegate().setResources(resources);
    }

    @Override
    public Map<String, String> getLanguageExtensions() {
        return getDelegate().getLanguageExtensions();
    }

    @Override
    public void setLanguageExtensions(final Map<String, String> languageExtensions) {
        getDelegate().setLanguageExtensions(languageExtensions);
    }

    @Override
    public Registry getRegistry() {
        return getDelegate().getRegistry();
    }

    @Override
    public void setRegistry(final Registry registry) {
        getDelegate().setRegistry(registry);
    }

    @Override
    public Dispatcher getDispatcher() {
        return getDelegate().getDispatcher();
    }

    @Override
    public void setDispatcher(final Dispatcher dispatcher) {
        getDelegate().setDispatcher(dispatcher);
    }

    @Override
    public ResteasyProviderFactory getProviderFactory() {
        return getDelegate().getProviderFactory();
    }

    @Override
    public void setProviderFactory(final ResteasyProviderFactory providerFactory) {
        getDelegate().setProviderFactory(providerFactory);
    }

    @Override
    public void setMediaTypeParamMapping(final String paramMapping) {
        getDelegate().setMediaTypeParamMapping(paramMapping);
    }

    @Override
    public List<ResourceFactory> getResourceFactories() {
        return getDelegate().getResourceFactories();
    }

    @Override
    public void setResourceFactories(final List<ResourceFactory> resourceFactories) {
        getDelegate().setResourceFactories(resourceFactories);
    }

    @Override
    public List<String> getUnwrappedExceptions() {
        return getDelegate().getUnwrappedExceptions();
    }

    @Override
    public void setUnwrappedExceptions(final List<String> unwrappedExceptions) {
        getDelegate().setUnwrappedExceptions(unwrappedExceptions);
    }

    @Override
    public Map<String, String> getConstructedDefaultContextObjects() {
        return getDelegate().getConstructedDefaultContextObjects();
    }

    @Override
    public void setConstructedDefaultContextObjects(final Map<String, String> constructedDefaultContextObjects) {
        getDelegate().setConstructedDefaultContextObjects(constructedDefaultContextObjects);
    }

    @Override
    public Map<Class, Object> getDefaultContextObjects() {
        return getDelegate().getDefaultContextObjects();
    }

    @Override
    public void setDefaultContextObjects(final Map<Class, Object> defaultContextObjects) {
        getDelegate().setDefaultContextObjects(defaultContextObjects);
    }

    @Override
    public List<String> getScannedResourceClasses() {
        return getDelegate().getScannedResourceClasses();
    }

    @Override
    public void setScannedResourceClasses(final List<String> scannedResourceClasses) {
        getDelegate().setScannedResourceClasses(scannedResourceClasses);
    }

    @Override
    public List<String> getScannedProviderClasses() {
        return getDelegate().getScannedProviderClasses();
    }

    @Override
    public void setScannedProviderClasses(final List<String> scannedProviderClasses) {
        getDelegate().setScannedProviderClasses(scannedProviderClasses);
    }

    @Override
    public List<String> getScannedJndiComponentResources() {
        return getDelegate().getScannedJndiComponentResources();
    }

    @Override
    public void setScannedJndiComponentResources(final List<String> scannedJndiComponentResources) {
        getDelegate().setScannedJndiComponentResources(scannedJndiComponentResources);
    }

    @Override
    public Map<String, List<String>> getScannedResourceClassesWithBuilder() {
        return getDelegate().getScannedResourceClassesWithBuilder();
    }

    @Override
    public void setScannedResourceClassesWithBuilder(
            final Map<String, List<String>> scannedResourceClassesWithBuilder) {
        getDelegate().setScannedResourceClassesWithBuilder(scannedResourceClassesWithBuilder);
    }

    @Override
    public boolean isWiderRequestMatching() {
        return getDelegate().isWiderRequestMatching();
    }

    @Override
    public void setWiderRequestMatching(final boolean widerRequestMatching) {
        getDelegate().setWiderRequestMatching(widerRequestMatching);
    }

    @Override
    public boolean isAddCharset() {
        return getDelegate().isAddCharset();
    }

    @Override
    public void setAddCharset(final boolean addCharset) {
        getDelegate().setAddCharset(addCharset);
    }

    @Override
    public InjectorFactory getInjectorFactory() {
        return getDelegate().getInjectorFactory();
    }

    @Override
    public void setInjectorFactory(final InjectorFactory injectorFactory) {
        getDelegate().setInjectorFactory(injectorFactory);
    }

    @Override
    public Object getProperty(final String key) {
        return getDelegate().getProperty(key);
    }

    @Override
    public void setProperty(final String key, final Object value) {
        getDelegate().setProperty(key, value);
    }

    @Override
    public void setStatisticsEnabled(final boolean statisticsEnabled) {
        getDelegate().setStatisticsEnabled(statisticsEnabled);
    }

    /**
     * Returns the delegate deployment.
     *
     * @return the delegate deployment
     */
    protected ResteasyDeployment getDelegate() {
        return delegate;
    }
}
