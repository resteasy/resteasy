package org.jboss.resteasy.plugins.server.reactor.netty;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.spi.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.netty.http.server.HttpServerResponse;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the 1-way bridge from RestEasy to reactor-netty's {@link
 * HttpServerResponse}.  Headers come via direct call.  RestEasy will write the
 * response body to the output stream it gets from {@link #getOutputStream}.
 */
public class ReactorNettyHttpResponse implements HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(ReactorNettyHttpResponse.class);

    private final HttpServerResponse resp;
    private OutputStream out;
    private boolean committed;
    private final MonoProcessor<Void> completionMono;

    public ReactorNettyHttpResponse(
        final HttpMethod method,
        final HttpServerResponse resp,
        final MonoProcessor<Void> completionMono
    ) {
        this.resp = resp;
        this.completionMono = completionMono;
        if (method == null || !method.equals(HttpMethod.HEAD)) {
            this.out = new ChunkOutputStream(this, resp, completionMono);
        } else {
            // Not entirely sure this is the best way to handle this..
            resp.responseHeaders().remove(HttpHeaderNames.TRANSFER_ENCODING);
            // TODO out is null; //[RESTEASY-1627] check this bug.
        }
    }

    @Override
    public int getStatus() {
        return resp.status().code();
    }

    @Override
    public void setStatus(int status) {
        resp.status(status);
    }

    @Override
    public MultivaluedMap<String, Object> getOutputHeaders() {
        final HttpHeaders headers = resp.responseHeaders();
        return new MultivaluedMap<String, Object>() {
            @Override
            public void putSingle(String key, Object value) {
                headers.remove(key);
                headers.add(key, value);
            }

            @Override
            public void add(String key, Object value) {
                headers.add(key, value);
            }

            @Override
            public Object getFirst(String key) {
                return headers.getAll(key).get(0);
            }

            @Override
            public void addAll(String key, Object... newValues) {
                headers.add(key, newValues);
            }

            @Override
            public void addAll(String key, List<Object> valueList) {
                headers.add(key, valueList);
            }

            @Override
            public void addFirst(String key, Object value) {
                headers.getAll(key).add(0, (String)value);
            }

            @Override
            public boolean equalsIgnoreValueOrder(MultivaluedMap<String, Object> otherMap) {
                throw new UnsupportedOperationException("TODO"); // TODO
            }

            @Override
            public int size() {
                return headers.size();
            }

            @Override
            public boolean isEmpty() {
                return headers.isEmpty();
            }

            @Override
            public boolean containsKey(Object key) {
                return headers.contains((String)key);
            }

            @Override
            public boolean containsValue(Object value) {
                throw new UnsupportedOperationException("TODO"); // TODO
            }

            @Override
            public List<Object> get(Object key) {
                throw new UnsupportedOperationException("TODO"); // TODO
            }

            @Override
            public List<Object> put(String key, List<Object> value) {
                throw new UnsupportedOperationException("TODO"); // TODO
            }

            @Override
            public List<Object> remove(Object key) {
                throw new UnsupportedOperationException("TODO"); // TODO
            }

            @Override
            public void putAll(Map<? extends String, ? extends List<Object>> m) {
                m.forEach(headers::add);
            }

            @Override
            public void clear() {
                headers.clear();
            }

            @Override
            public Set<String> keySet() {
                return headers.names();
            }

            @Override
            public Collection<List<Object>> values() {
                throw new UnsupportedOperationException("TODO"); // TODO
            }

            @Override
            public Set<Entry<String, List<Object>>> entrySet() {
                throw new UnsupportedOperationException("TODO"); // TODO
            }
        };
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    @Override
    public void setOutputStream(OutputStream os) {
        // TODO what is this about?
        out = os;
    }

    @Override
    public void addNewCookie(NewCookie cookie)
    {
        resp.responseHeaders().add(javax.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
    }

    @Override
    public void sendError(int status) {
        log.trace("Sending error");
        resp.status(status)
            .header(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO)
            .then().subscribe(completionMono);
        committed = true;
    }

    @Override
    public void sendError(int status, String message) {
        log.trace("Sending error: " + message);
        resp.status(status)
            .header(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(message.length()))
            .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
            .sendString(Mono.just(message))
            .then()
            .subscribe(completionMono);
        committed();
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    void committed() {
        committed = true;
    }

    @Override
    public void reset() {
        // TODO
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        } else {
            Mono.<Void>empty().subscribe(completionMono);
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        log.trace("Flushing response buffer!");
        out.flush();
    }
}
