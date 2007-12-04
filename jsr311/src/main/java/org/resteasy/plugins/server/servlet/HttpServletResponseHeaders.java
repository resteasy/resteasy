package org.resteasy.plugins.server.servlet;

import org.resteasy.specimpl.MultivaluedMapImpl;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletResponseHeaders implements MultivaluedMap<String, String> {

    private MultivaluedMap<String, String> cachedHeaders = new MultivaluedMapImpl<String, String>();
    private HttpServletResponse response;

    public HttpServletResponseHeaders(HttpServletResponse response) {
        this.response = response;
    }

    public void putSingle(String key, String value) {
        cachedHeaders.putSingle(key, value);
        response.addHeader(key, value);
    }

    public void add(String key, String value) {
        cachedHeaders.add(key, value);
        response.addHeader(key, value);
    }

    public String getFirst(String key) {
        return cachedHeaders.getFirst(key);
    }

    public int size() {
        return cachedHeaders.size();
    }

    public boolean isEmpty() {
        return cachedHeaders.isEmpty();
    }

    public boolean containsKey(Object o) {
        return cachedHeaders.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return cachedHeaders.containsValue(o);
    }

    public List<String> get(Object o) {
        return cachedHeaders.get(o);
    }

    public List<String> put(String s, List<String> strings) {
        for (String string : strings) {
            response.addHeader(s, string);
        }
        return cachedHeaders.put(s, strings);
    }

    public List<String> remove(Object o) {
        throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
    }

    public void putAll(Map<? extends String, ? extends List<String>> map) {
        throw new RuntimeException("putAll() on this class not supported yet");
    }

    public void clear() {
        throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
    }

    public Set<String> keySet() {
        return cachedHeaders.keySet();
    }

    public Collection<List<String>> values() {
        return cachedHeaders.values();
    }

    public Set<Entry<String, List<String>>> entrySet() {
        return cachedHeaders.entrySet();
    }

    public boolean equals(Object o) {
        return cachedHeaders.equals(o);
    }

    public int hashCode() {
        return cachedHeaders.hashCode();
    }
}
