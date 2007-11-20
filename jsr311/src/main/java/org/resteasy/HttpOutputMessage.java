package org.resteasy;

import org.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import java.io.OutputStream;

/**
 * Abstraction for output HTTP requests
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpOutputMessage {
    protected MultivaluedMap<String, String> outputHeaders = new MultivaluedMapImpl<String, String>();
    protected OutputStream outputStream;

    public HttpOutputMessage(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public MultivaluedMap<String, String> getOutputHeaders() {
        return outputHeaders;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
