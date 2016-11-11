package org.jboss.resteasy.test.core.interceptors.resource;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

@Provider
public class PreProcessorExceptionMapperPreProcessSecurityInterceptor implements PreProcessInterceptor {
    public ServerResponse preProcess(HttpRequest request, ResourceMethodInvoker method) throws Failure, WebApplicationException {
        throw new PreProcessorExceptionMapperCandlepinUnauthorizedException();
    }
}
