package org.jboss.resteasy.spi.metadata;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DefaultResourceMethod extends DefaultResourceLocator implements ResourceMethod {
    private static final MediaType[] empty = {};
    protected Set<String> httpMethods = new HashSet<String>();
    protected MediaType[] produces = empty;
    protected MediaType[] consumes = empty;
    protected boolean asynchronous;

    public DefaultResourceMethod(final ResourceClass declaredClass, final Method method, final Method annotatedMethod) {
        super(declaredClass, method, annotatedMethod);
    }

    @Override
    public Set<String> getHttpMethods() {
        return httpMethods;
    }

    @Override
    public MediaType[] getProduces() {
        return produces;
    }

    @Override
    public MediaType[] getConsumes() {
        return consumes;
    }

    @Override
    public boolean isAsynchronous() {
        return asynchronous;
    }

    @Override
    public void markAsynchronous() {
        asynchronous = true;
    }
}
