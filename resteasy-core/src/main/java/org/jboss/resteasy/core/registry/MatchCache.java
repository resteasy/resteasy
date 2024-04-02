package org.jboss.resteasy.core.registry;

import java.util.List;
import java.util.Objects;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResourceInvoker;

public class MatchCache {
    public MediaType chosen;
    public SegmentNode.Match match;
    public ResourceInvoker invoker;

    private final String pathExpression;

    /**
     * Use the MatchCache(String) constructor
     *
     * @see #MatchCache(String)
     */
    @Deprecated(forRemoval = true, since = "7.0")
    public MatchCache() {
        this("/");
    }

    /**
     * Creates a new match cache with the path expression for the match. The path expression is used in the
     * {@link UriInfo#getMatchedResourceTemplate()}.
     *
     * @param pathExpression the path expression from the {@link SegmentNode.Match#expression}
     */
    protected MatchCache(final String pathExpression) {
        this.pathExpression = (pathExpression == null) ? "/" : pathExpression;
    }

    /**
     * Returns the path expression for this match.
     *
     * @return the path expression
     */
    protected String pathExpression() {
        return pathExpression;
    }

    public static class Key {
        public String path;
        public int start;
        public String method;
        public MediaType contentType;
        public List<MediaType> accepts;

        public Key(final HttpRequest request, final int start) {
            String matchingPath = ((ResteasyUriInfo) request.getUri()).getMatchingPath();
            this.path = start == 0 ? matchingPath : matchingPath.substring(start);
            this.start = start;
            this.method = request.getHttpMethod();
            this.contentType = request.getHttpHeaders().getMediaType();
            this.accepts = request.getHttpHeaders().getAcceptableMediaTypes();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Key key = (Key) o;
            boolean b = start == key.start &&
                    path.equals(key.path) &&
                    method.equals(key.method) &&
                    Objects.equals(contentType, key.contentType);
            if (!b)
                return false;
            if (accepts.isEmpty() && key.accepts.isEmpty())
                return true;
            if (accepts.size() != key.accepts.size())
                return false;
            // todo improve this
            return b &&
                    accepts.equals(key.accepts);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, start, method);
        }
    }
}
