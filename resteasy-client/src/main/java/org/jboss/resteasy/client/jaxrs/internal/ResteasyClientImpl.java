package org.jboss.resteasy.client.jaxrs.internal;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.concurrent.ContextualScheduledExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyClientImpl implements ResteasyClient {
    protected final ClientHttpEngine httpEngine;
    private final ExecutorService asyncInvocationExecutor;
    private final ContextualScheduledExecutorService scheduledExecutorService;
    protected ClientConfiguration configuration;
    protected boolean closed;
    protected boolean cleanupExecutor;

    protected ResteasyClientImpl(final ClientHttpEngine httpEngine, final ExecutorService asyncInvocationExecutor,
            final boolean cleanupExecutor,
            final ScheduledExecutorService scheduledExecutorService, final ClientConfiguration configuration) {
        this(httpEngine, asyncInvocationExecutor, cleanupExecutor, ContextualExecutors.wrap(scheduledExecutorService),
                configuration);
    }

    protected ResteasyClientImpl(final ClientHttpEngine httpEngine, final ExecutorService asyncInvocationExecutor,
            final boolean cleanupExecutor,
            final ContextualScheduledExecutorService scheduledExecutorService, final ClientConfiguration configuration) {
        this.cleanupExecutor = cleanupExecutor;
        this.httpEngine = httpEngine;
        this.asyncInvocationExecutor = asyncInvocationExecutor;
        this.configuration = configuration;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    protected ResteasyClientImpl(final ClientHttpEngine httpEngine, final ExecutorService asyncInvocationExecutor,
            final boolean cleanupExecutor, final ClientConfiguration configuration) {
        this(httpEngine, asyncInvocationExecutor, cleanupExecutor, ContextualExecutors.scheduledThreadPool(), configuration);
    }

    public ClientHttpEngine httpEngine() {
        abortIfClosed();
        return httpEngine;
    }

    public ExecutorService asyncInvocationExecutor() {
        return asyncInvocationExecutor;
    }

    public ScheduledExecutorService getScheduledExecutor() {
        return this.scheduledExecutorService;
    }

    public void abortIfClosed() {
        if (isClosed())
            throw new IllegalStateException(Messages.MESSAGES.clientIsClosed());
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        closed = true;
        try {
            httpEngine.close();
            if (cleanupExecutor) {
                asyncInvocationExecutor.shutdown();
            }
            if (scheduledExecutorService != null && !scheduledExecutorService.isManaged()) {
                scheduledExecutorService.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Configuration getConfiguration() {
        abortIfClosed();
        return configuration;
    }

    @Override
    public SSLContext getSslContext() {
        abortIfClosed();
        return httpEngine().getSslContext();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        abortIfClosed();
        return httpEngine().getHostnameVerifier();
    }

    @Override
    public ResteasyClientImpl property(String name, Object value) {
        abortIfClosed();
        configuration.property(name, value);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Class<?> componentClass) {
        abortIfClosed();
        configuration.register(componentClass);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Class<?> componentClass, int priority) {
        abortIfClosed();
        configuration.register(componentClass, priority);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Class<?> componentClass, Class<?>... contracts) {
        abortIfClosed();
        configuration.register(componentClass, contracts);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        abortIfClosed();
        configuration.register(componentClass, contracts);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Object component) {
        abortIfClosed();
        configuration.register(component);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Object component, int priority) {
        abortIfClosed();
        configuration.register(component, priority);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Object component, Class<?>... contracts) {
        abortIfClosed();
        configuration.register(component, contracts);
        return this;
    }

    @Override
    public ResteasyClientImpl register(Object component, Map<Class<?>, Integer> contracts) {
        abortIfClosed();
        configuration.register(component, contracts);
        return this;
    }

    @Override
    public ResteasyWebTarget target(String uri) throws IllegalArgumentException, NullPointerException {
        abortIfClosed();
        if (uri == null)
            throw new NullPointerException(Messages.MESSAGES.uriWasNull());
        return createClientWebTarget(this, uri, configuration);
    }

    @Override
    public ResteasyWebTarget target(URI uri) throws NullPointerException {
        abortIfClosed();
        if (uri == null)
            throw new NullPointerException(Messages.MESSAGES.uriWasNull());
        return createClientWebTarget(this, uri, configuration);
    }

    @Override
    public ResteasyWebTarget target(UriBuilder uriBuilder) throws NullPointerException {
        abortIfClosed();
        if (uriBuilder == null)
            throw new NullPointerException(Messages.MESSAGES.uriBuilderWasNull());
        return createClientWebTarget(this, uriBuilder, configuration);
    }

    @Override
    public ResteasyWebTarget target(Link link) throws NullPointerException {
        abortIfClosed();
        if (link == null)
            throw new NullPointerException(Messages.MESSAGES.linkWasNull());
        URI uri = link.getUri();
        return createClientWebTarget(this, uri, configuration);
    }

    @Override
    public Invocation.Builder invocation(Link link) throws NullPointerException, IllegalArgumentException {
        abortIfClosed();
        if (link == null)
            throw new NullPointerException(Messages.MESSAGES.linkWasNull());
        WebTarget target = target(link);
        if (link.getType() != null)
            return target.request(link.getType());
        else
            return target.request();

    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, String uri,
            ClientConfiguration configuration) {
        return new ClientWebTarget(client, uri, configuration);
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, URI uri, ClientConfiguration configuration) {
        return new ClientWebTarget(client, uri, configuration);
    }

    protected ResteasyWebTarget createClientWebTarget(ResteasyClientImpl client, UriBuilder uriBuilder,
            ClientConfiguration configuration) {
        return new ClientWebTarget(client, uriBuilder, configuration);
    }
}
