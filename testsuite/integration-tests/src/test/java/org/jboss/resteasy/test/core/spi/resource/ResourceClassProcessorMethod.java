package org.jboss.resteasy.test.core.spi.resource;

import org.jboss.resteasy.spi.metadata.MethodParameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ResourceClassProcessorMethod implements ResourceMethod {

    private final ResourceMethod delegate;
    private final MediaType[] produces = new MediaType[]{MediaType.APPLICATION_XML_TYPE};

    public ResourceClassProcessorMethod(ResourceMethod delegate) {
        this.delegate = delegate;
    }

    @Override
    public Set<String> getHttpMethods() {
        Set<String> methods = new HashSet<>();
        methods.add("GET");
        return methods;
    }

    @Override
    public MediaType[] getProduces() {
        return produces;
    }

    @Override
    public MediaType[] getConsumes() {
        return delegate.getConsumes();
    }

    @Override
    public boolean isAsynchronous() {
        return delegate.isAsynchronous();
    }

    @Override
    public void markAsynchronous() {
        delegate.markAsynchronous();
    }

    @Override
    public ResourceClass getResourceClass() {
        return delegate.getResourceClass();
    }

    @Override
    public Class<?> getReturnType() {
        return delegate.getReturnType();
    }

    @Override
    public Type getGenericReturnType() {
        return delegate.getGenericReturnType();
    }

    @Override
    public Method getMethod() {
        return delegate.getMethod();
    }

    @Override
    public Method getAnnotatedMethod() {
        return delegate.getAnnotatedMethod();
    }

    @Override
    public MethodParameter[] getParams() {
        return delegate.getParams();
    }

    @Override
    public String getFullpath() {
        return delegate.getFullpath();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }
}
