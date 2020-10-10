package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.core.Dispatcher;
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

        Config config = ConfigProvider.getConfig();
        boolean originalBehavior = config.getOptionalValue(ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR, boolean.class).orElse(false);
        boolean serverSide = ResteasyProviderFactory.searchContextData(Dispatcher.class) != null;
        if (originalBehavior || !serverSide) {
           return new WebApplicationException("Unknown error, status code " + response.getStatus(), response);
        } else {
           return new ResteasyWebApplicationException("Unknown error, status code " + response.getStatus(), response);
        }
    }

    @Override
    public boolean handles(int status, MultivaluedMap headers) {
        return status >= 300;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
