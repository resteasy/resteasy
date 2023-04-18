package org.jboss.resteasy.plugins.server.reactor.netty;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.reactor.netty.i18n.Messages;
import org.jboss.resteasy.spi.HttpResponse;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.server.HttpServerResponse;

/**
 * This is the 1-way bridge from RestEasy to reactor-netty's {@link
 * HttpServerResponse}. Headers come via direct call. RestEasy will write the
 * response body to the output stream it gets from {@link #getOutputStream}.
 */
public class ReactorNettyHttpResponse implements HttpResponse {
    private static final Logger log = Logger.getLogger(ReactorNettyHttpResponse.class);

    private final HttpServerResponse resp;
    private OutputStream out;
    private boolean committed;
    private final Sinks.Empty<Void> completionSink;

    public ReactorNettyHttpResponse(
            final HttpMethod method,
            final HttpServerResponse resp,
            final Sinks.Empty<Void> completionSink) {
        this.resp = resp;
        this.completionSink = completionSink;
        if (method == null || !method.equals(HttpMethod.HEAD)) {
            this.out = new ChunkOutputStream(this, resp, completionSink);
        } else {
            resp.responseHeaders().remove(HttpHeaderNames.TRANSFER_ENCODING);
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
                final List<String> values = headers.getAll(key);
                if (values.isEmpty()) {
                    return null;
                }
                return values.get(0);
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
                headers.getAll(key).add(0, (String) value);
            }

            @Override
            public boolean equalsIgnoreValueOrder(MultivaluedMap<String, Object> otherMap) {
                throw new UnsupportedOperationException();
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
                return headers.contains((String) key);
            }

            @Override
            public boolean containsValue(Object value) {
                return headers.entries().stream().anyMatch(e -> e.getValue().equals(value));
            }

            @Override
            public List<Object> get(Object key) {
                // We could also do the following, which is potentially a safer
                // option; however, it would do copy.  Though, not a deep copy,
                // it would copy the references.  But, still, it would be
                // unnecessary.
                // return new ArrayList<>(headers.getAll(key.toString()));

                if (key == null) {
                    return null;
                }

                return (List) headers.getAll(key.toString());
            }

            @Override
            public List<Object> put(String key, List<Object> value) {
                final List<Object> previous = get(key);
                headers.add(key, value);
                return previous;
            }

            @Override
            public List<Object> remove(Object key) {
                final List<Object> previous = get(key);
                headers.remove(key.toString());
                return previous;
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
                return headers.entries()
                        .stream()
                        .map(e -> get(e.getKey()))
                        .collect(Collectors.toList());
            }

            /**
             * Please note, this method is quite costly. It is a better
             * to iterate over keys and lookup values.
             *
             * @return
             */
            @Override
            public Set<Entry<String, List<Object>>> entrySet() {
                final Set<Entry<String, List<Object>>> entries = new HashSet<Entry<String, List<Object>>>();

                headers.names().forEach(name -> entries.add(new Entry<String, List<Object>>() {
                    @Override
                    public String getKey() {
                        return name;
                    }

                    @Override
                    public List<Object> getValue() {
                        return get(name);
                    }

                    @Override
                    public List<Object> setValue(List<Object> value) {
                        throw new UnsupportedOperationException("Read Only Entry!");
                    }
                }));

                return entries;
            }
        };
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    @Override
    public void setOutputStream(OutputStream os) {
        // I guess one scenario where this method could be called
        // is modifying the response in a ContainerResponseFilter.
        // We should probably make sure to close the existing
        // OutputStream (if exists one) to prevent leaks.
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                log.warn("Failed to close OutputStream", e);
            }
        }

        out = os;
    }

    @Override
    public void addNewCookie(NewCookie cookie) {
        resp.responseHeaders().add(jakarta.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
    }

    @Override
    public void sendError(int status) {
        final Mono<Void> respMono = resp.status(status)
                .header(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO)
                .then();

        committed = true;
        SinkSubscriber.subscribe(completionSink, respMono);
        committed();

    }

    @Override
    public void sendError(int status, String message) {
        final Mono<Void> respMono = resp.status(status)
                .header(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(message.length()))
                .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                .sendString(Mono.just(message))
                .then();

        SinkSubscriber.subscribe(completionSink, respMono);
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

        if (committed) {
            throw new IllegalStateException(Messages.MESSAGES.alreadyCommitted());
        }

        resp.responseHeaders().clear();
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        } else {
            SinkSubscriber.subscribe(completionSink, Mono.<Void> empty());
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        out.flush();
    }

}
