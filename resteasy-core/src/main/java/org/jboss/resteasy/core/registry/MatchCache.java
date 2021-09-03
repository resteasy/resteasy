package org.jboss.resteasy.core.registry;


import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResourceInvoker;

import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Objects;

public class MatchCache {
    public MediaType chosen;
    public SegmentNode.Match match;
    public ResourceInvoker invoker;

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
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            boolean b = start == key.start &&
                    path.equals(key.path) &&
                    method.equals(key.method) &&
                    Objects.equals(contentType, key.contentType);
            if (!b) return false;
            if (accepts.isEmpty() && key.accepts.isEmpty()) return true;
            if (accepts.size() != key.accepts.size()) return false;
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
