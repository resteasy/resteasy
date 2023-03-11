package org.jboss.resteasy.util;

import java.util.Map;

import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.FeatureContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FeatureContextDelegate implements FeatureContext {
    protected Configurable<?> configurable;

    public FeatureContextDelegate(final Configurable<?> configurable) {
        this.configurable = configurable;
    }

    @Override
    public Configuration getConfiguration() {
        return configurable.getConfiguration();
    }

    @Override
    public FeatureContext property(String name, Object value) {
        configurable.property(name, value);
        return this;
    }

    @Override
    public FeatureContext register(Class<?> componentClass) {
        configurable.register(componentClass);
        return this;
    }

    @Override
    public FeatureContext register(Class<?> componentClass, int priority) {
        configurable.register(componentClass, priority);
        return this;
    }

    @Override
    public FeatureContext register(Class<?> componentClass, Class<?>... contracts) {
        configurable.register(componentClass, contracts);
        return this;
    }

    @Override
    public FeatureContext register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        configurable.register(componentClass, contracts);
        return this;
    }

    @Override
    public FeatureContext register(Object component) {
        configurable.register(component);
        return this;
    }

    @Override
    public FeatureContext register(Object component, int priority) {
        configurable.register(component, priority);
        return this;
    }

    @Override
    public FeatureContext register(Object component, Class<?>... contracts) {
        configurable.register(component, contracts);
        return this;
    }

    @Override
    public FeatureContext register(Object component, Map<Class<?>, Integer> contracts) {
        configurable.register(component, contracts);
        return this;
    }
}
