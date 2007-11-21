package org.resteasy;

import org.resteasy.spi.HttpInputMessage;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpContextParameter implements ParameterExtractor {
    private Class type;

    public HttpContextParameter(Class type) {
        this.type = type;
    }

    public Object extract(HttpInputMessage request) {
        if (type.equals(HttpHeaders.class)) return request.getHttpHeaders();
        if (type.equals(UriInfo.class)) return request.getUri();
        return null;
    }
}
