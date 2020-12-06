package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.microprofile.config.ResteasyConfig;
import org.jboss.resteasy.microprofile.config.ResteasyConfigFactory;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class DefaultResponseExceptionMapper implements ResponseExceptionMapper {

    @Override
    public Throwable toThrowable(Response response) {
        try {
            response.bufferEntity();
        } catch (Exception ignored) {}
        return WebApplicationExceptionWrapper.wrap(new WebApplicationException("Unknown error, status code " + response.getStatus(), response));
    }

    @Override
    public boolean handles(int status, MultivaluedMap headers) {
        final ResteasyConfig config = ResteasyConfigFactory.getConfig();
        final boolean originalBehavior = Boolean.parseBoolean(config.getValue(ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR,
               ResteasyConfig.SOURCE.SERVLET_CONTEXT, "false"));
        final boolean serverSide = ResteasyProviderFactory.searchContextData(Dispatcher.class) != null;
        return status >= (originalBehavior || !serverSide ? 400 : 300);
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
