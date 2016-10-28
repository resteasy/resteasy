package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Type;

@Provider
public class ResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (requestContext.getUriInfo().getPath().endsWith("getstatus")) {
            int status = responseContext.getStatus();
            responseContext.setStatus(Response.Status.OK.getStatusCode());
            responseContext.setEntity(String.valueOf(status), null, MediaType.TEXT_PLAIN_TYPE);
        } else if (requestContext.getUriInfo().getPath().endsWith("getentitytype")) {
            Type type = responseContext.getEntityType();
            String name = "NULL";
            if (type instanceof Class) {
                name = ((Class<?>) type).getName();
            } else if (type != null) {
                name = type.getClass().getName();
            }
            responseContext.setEntity(name, null, MediaType.TEXT_PLAIN_TYPE);

        } else if (requestContext.getUriInfo().getPath().endsWith("getstatusinfo")) {
            Response.StatusType type = responseContext.getStatusInfo();
            if (type == null) {
                responseContext.setEntity("NULL", null, MediaType.TEXT_PLAIN_TYPE);
                responseContext.setStatus(Response.Status.OK.getStatusCode());
                return;
            }
            int status = type.getStatusCode();
            responseContext.setStatus(Response.Status.OK.getStatusCode());
            responseContext.setEntity(String.valueOf(status), null, MediaType.TEXT_PLAIN_TYPE);

        }
    }
}
