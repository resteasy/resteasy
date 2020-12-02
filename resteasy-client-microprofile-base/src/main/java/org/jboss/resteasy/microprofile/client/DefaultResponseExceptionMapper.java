package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyDeployment;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

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
       final Config config = ConfigProvider.getConfig();
       final boolean originalBehavior = config.getOptionalValue(ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR, boolean.class).orElse(false);
       final boolean serverSide = ResteasyDeployment.onServer();
       return status >= (originalBehavior || !serverSide ? 400 : 300);
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
