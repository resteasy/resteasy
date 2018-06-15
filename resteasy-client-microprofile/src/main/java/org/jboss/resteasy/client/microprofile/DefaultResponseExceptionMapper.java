package org.jboss.resteasy.client.microprofile;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

/**
 * Created by hbraun on 17.01.18.
 */
public class DefaultResponseExceptionMapper implements ResponseExceptionMapper {


    @Override
    public Throwable toThrowable(Response response) {
        return new WebApplicationException("Unkown error, status code " + response.getStatus(), response);
    }

    @Override
    public boolean handles(int status, MultivaluedMap headers) {
        return status>=400;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
