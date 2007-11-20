package org.resteasy;

import org.resteasy.specimpl.UriInfoImpl;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;

/**
 * Abstraction for an inbound http request on the server, or a response from a server to a client
 * <p/>
 * We have this abstraction so that we can reuse marshalling objects in a client framework and serverside framework
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpInputMessage {
    protected HttpHeaders httpHeaders;
    protected InputStream inputStream;
    protected UriInfoImpl uri;
    protected MultivaluedMap<String, String> parameters;

    public HttpInputMessage(HttpHeaders httpHeaders, InputStream inputStream, String path, MultivaluedMap<String, String> parameters) {
        this.httpHeaders = httpHeaders;
        this.inputStream = inputStream;
        this.uri = new UriInfoImpl(path);
        this.parameters = parameters;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public UriInfoImpl getUri() {
        return uri;
    }

    public MultivaluedMap<String, String> getParameters() {
        return parameters;
    }
}
