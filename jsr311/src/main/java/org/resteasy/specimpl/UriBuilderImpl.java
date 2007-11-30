package org.resteasy.specimpl;

import org.resteasy.util.PathHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriBuilderImpl extends UriBuilder {
    private String host;
    private String scheme;
    private String ssp;
    private int port = -1;

    // todo need to implement encoding
    private boolean encode;

    private String userInfo;
    private String path;
    private String matrix;
    private String query;
    private String fragment;


    public UriBuilder clone() {
        UriBuilderImpl impl = new UriBuilderImpl();
        impl.host = host;
        impl.scheme = scheme;
        impl.ssp = ssp;
        impl.port = port;
        impl.encode = encode;
        impl.userInfo = userInfo;
        impl.path = path;
        impl.matrix = matrix;
        impl.query = query;
        impl.fragment = fragment;

        return impl;
    }

    public UriBuilder encode(boolean enable) {
        encode = enable;
        return this;
    }

    public UriBuilder uri(URI uri) throws IllegalArgumentException {
        host = uri.getHost();
        scheme = uri.getScheme();
        ssp = uri.getSchemeSpecificPart();
        port = uri.getPort();
        userInfo = uri.getUserInfo();
        path = uri.getPath();
        if (path == null) {
            int idx = path.indexOf(';');
            if (idx > -1) {
                matrix = path.substring(idx);
                path = path.substring(0, idx);
            }
        }
        fragment = uri.getFragment();
        query = uri.getQuery();
        return this;
    }

    public UriBuilder scheme(String scheme) throws IllegalArgumentException {
        this.scheme = scheme;
        return this;
    }

    public UriBuilder schemeSpecificPart(String ssp) throws IllegalArgumentException {
        this.ssp = ssp;
        return this;
    }

    public UriBuilder userInfo(String ui) throws IllegalArgumentException {
        this.userInfo = ui;
        return this;
    }

    public UriBuilder host(String host) throws IllegalArgumentException {
        this.host = host;
        return this;
    }

    public UriBuilder port(int port) throws IllegalArgumentException {
        this.port = port;
        return this;
    }

    public UriBuilder replacePath(String path) throws IllegalArgumentException {
        this.path = path;
        if (!path.startsWith("/")) this.path = "/" + path;
        return this;
    }

    public UriBuilder path(String... segments) throws IllegalArgumentException {
        if (path == null) path = "";
        StringBuilder builder = new StringBuilder(path);
        for (String segment : segments) {
            builder.append("/");
            if (segment.startsWith("/")) segment = segment.substring(1);
            builder.append(segment);
        }
        path = builder.toString();
        return this;
    }

    public UriBuilder path(Class resource) throws IllegalArgumentException {
        Path ann = (Path) resource.getAnnotation(Path.class);
        if (ann != null) path(ann.value());
        return this;
    }

    public UriBuilder path(Class resource, String method) throws IllegalArgumentException {
        for (Method m : resource.getMethods()) {
            if (m.getName().equals(method)) {
                return path(m);
            }
        }
        return this;
    }

    public UriBuilder path(Method... methods) throws IllegalArgumentException {
        for (Method method : methods) {
            Path ann = method.getAnnotation(Path.class);
            if (ann != null) path(ann.value());
        }
        return this;
    }

    public UriBuilder replaceMatrixParams(String matrix) throws IllegalArgumentException {
        this.matrix = matrix;
        return this;
    }

    public UriBuilder matrixParam(String name, String value) throws IllegalArgumentException {
        if (this.matrix == null) matrix = "";
        StringBuilder tmp = new StringBuilder(this.matrix);
        tmp.append(";").append(name).append("=").append(value);
        matrix = tmp.toString();
        return this;
    }

    public UriBuilder replaceQueryParams(String query) throws IllegalArgumentException {
        this.query = query;
        return this;
    }

    public UriBuilder queryParam(String name, String value) throws IllegalArgumentException {
        if (query == null) query = name + "=" + value;
        else query += "&" + name + "=" + value;
        return this;
    }

    public UriBuilder fragment(String fragment) throws IllegalArgumentException {
        this.fragment = fragment;
        return this;
    }

    /**
     * Replace first found uri parameter of name with give value
     *
     * @param name
     * @param value
     * @return
     * @throws IllegalArgumentException if name or value is null or
     *                                  if automatic encoding is disabled the paramter value contains illegal characters
     */
    public UriBuilder uriParam(String name, String value) throws IllegalArgumentException {
        if (path == null) return this;
        String[] paths = path.split("/");
        int i = 0;
        for (String p : paths) {
            Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
            if (matcher.matches()) {
                String uriParamName = matcher.group(2);
                if (uriParamName.equals(name)) {
                    paths[i] = value;
                    break;
                }
            }
            i++;
        }
        path = null;
        path(paths);
        return this;
    }


    public URI build() throws UriBuilderException {
        try {
            String tmpPath = path;
            if (matrix != null) tmpPath += matrix;
            return new URI(scheme, userInfo, host, port, tmpPath, query, fragment);
        } catch (URISyntaxException e) {
            throw new UriBuilderException(e);
        }
    }

    public URI build(Map<String, String> values) throws IllegalArgumentException, UriBuilderException {
        if (values.size() > 0 || path == null) return build();
        String[] paths = path.split("/");
        int i = 0;
        for (String p : paths) {
            Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
            if (matcher.matches()) {
                String uriParamName = matcher.group(2);
                String value = values.get(uriParamName);
                if (value == null)
                    throw new IllegalArgumentException("uri parameter {" + uriParamName + "} does not exist as a value");
                paths[i] = value;
            }
            i++;
        }
        path = null;
        path(paths);
        return build();
    }

    public URI build(String... values) throws IllegalArgumentException, UriBuilderException {
        if (values.length > 0 || path == null) return build();
        String[] paths = path.split("/");
        int i = 0, j = 0;
        for (String p : paths) {
            Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
            if (matcher.matches()) {
                if (j >= values.length)
                    throw new IllegalArgumentException("Not enough values passed in to fill all parameters");
                paths[i] = values[j++];
            }
            i++;
        }
        path = null;
        path(paths);
        return build();
    }
}
