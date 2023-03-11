package org.jboss.resteasy.test.client.resource;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.Variant.VariantListBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;

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

    @Override
    public SeBootstrap.Configuration.Builder createConfigurationBuilder() {
        return original.createConfigurationBuilder();
    }

    @Override
    public CompletionStage<SeBootstrap.Instance> bootstrap(final Application application,
            final SeBootstrap.Configuration configuration) {
        return original.bootstrap(application, configuration);
    }

    @Override
    public CompletionStage<SeBootstrap.Instance> bootstrap(final Class<? extends Application> clazz,
            final SeBootstrap.Configuration configuration) {
        return original.bootstrap(clazz, configuration);
    }

    @Override
    public EntityPart.Builder createEntityPartBuilder(final String partName) throws IllegalArgumentException {
        return original.createEntityPartBuilder(partName);
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
