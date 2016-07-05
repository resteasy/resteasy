package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

public class StringBeanRuntimeDelegate extends RuntimeDelegate {

    public StringBeanRuntimeDelegate(final RuntimeDelegate orig) {
        super();
        this.original = orig;
        assertNotStringBeanRuntimeDelegate(orig);
    }

    private RuntimeDelegate original;

    @Override
    public <T> T createEndpoint(Application arg0, Class<T> arg1)
            throws IllegalArgumentException, UnsupportedOperationException {
        return original.createEndpoint(arg0, arg1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> arg0)
            throws IllegalArgumentException {
        if (arg0 == StringBean.class) {
            return (HeaderDelegate<T>) new StringBeanHeaderDelegate();
        } else {
            return original.createHeaderDelegate(arg0);
        }
    }

    @Override
    public ResponseBuilder createResponseBuilder() {
        return original.createResponseBuilder();
    }

    @Override
    public UriBuilder createUriBuilder() {
        return original.createUriBuilder();
    }

    @Override
    public VariantListBuilder createVariantListBuilder() {
        return original.createVariantListBuilder();
    }

    public RuntimeDelegate getOriginal() {
        return original;
    }

    public static final void assertNotStringBeanRuntimeDelegate() {
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();
        assertNotStringBeanRuntimeDelegate(delegate);
    }

    public static final void assertNotStringBeanRuntimeDelegate(RuntimeDelegate delegate) {
        if (delegate instanceof StringBeanRuntimeDelegate) {
            StringBeanRuntimeDelegate sbrd = (StringBeanRuntimeDelegate) delegate;
            if (sbrd.getOriginal() != null) {
                RuntimeDelegate.setInstance(sbrd.getOriginal());
            }
            throw new RuntimeException(
                    "RuntimeDelegate.getInstance() is StringBeanRuntimeDelegate");
        }
    }

    @Override
    public Builder createLinkBuilder() {
        return original.createLinkBuilder();
    }
}