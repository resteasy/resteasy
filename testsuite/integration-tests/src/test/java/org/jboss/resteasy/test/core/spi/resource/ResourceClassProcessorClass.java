package org.jboss.resteasy.test.core.spi.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.FieldParameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.spi.metadata.SetterParameter;

import java.util.ArrayList;

public class ResourceClassProcessorClass implements ResourceClass {

    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorClass.class.getName());

    private final ResourceClass delegate;
    private final ResourceMethod[] resourceMethods;

    public ResourceClassProcessorClass(ResourceClass delegate) {
        logger.info(String.format("ResourceClassProcessorClass constructor called for %s class", delegate.getClazz().getSimpleName()));
        this.delegate = delegate;

        ArrayList<ResourceMethod> methods = new ArrayList<>();
        for (ResourceMethod method : this.delegate.getResourceMethods()) {
            if (method.getMethod().getName().equals("custom")) {
                methods.add(new ResourceClassProcessorMethod(method));
            } else {
                methods.add(method);
            }
        }
        this.resourceMethods = methods.toArray(new ResourceMethod[0]);
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public Class<?> getClazz() {
        return delegate.getClazz();
    }

    @Override
    public ResourceConstructor getConstructor() {
        return delegate.getConstructor();
    }

    @Override
    public FieldParameter[] getFields() {
        return delegate.getFields();
    }

    @Override
    public SetterParameter[] getSetters() {
        return delegate.getSetters();
    }

    @Override
    public ResourceMethod[] getResourceMethods() {
        return resourceMethods;
    }

    @Override
    public ResourceLocator[] getResourceLocators() {
        return delegate.getResourceLocators();
    }
}
