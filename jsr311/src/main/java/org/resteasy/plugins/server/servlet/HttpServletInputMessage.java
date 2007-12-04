package org.resteasy.plugins.server.servlet;

import org.resteasy.spi.HttpInput;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * Abstraction for an inbound http request on the server, or a response from a server to a client
 * <p/>
 * We have this abstraction so that we can reuse marshalling objects in a client framework and serverside framework
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletInputMessage implements HttpInput {
    protected HttpHeaders httpHeaders;
    protected InputStream inputStream;
    protected UriInfo uri;
    protected MultivaluedMap<String, String> parameters;

    public HttpServletInputMessage(HttpHeaders httpHeaders, InputStream inputStream, UriInfo uri, MultivaluedMap<String, String> parameters) {
        this.httpHeaders = httpHeaders;
        this.inputStream = inputStream;
        this.uri = uri;
        this.parameters = parameters;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public UriInfo getUri() {
        return uri;
    }

    public MultivaluedMap<String, String> getParameters() {
        return parameters;
    }
}
