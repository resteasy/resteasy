package org.jboss.resteasy.core;

import java.net.URI;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * <p>
 * InternalDispatch represents a "forward" in servlet terms. You can perform an
 * internal GET/POST/DELETE/PUT using IntenalDispatch using Java Object. For
 * example:
 * </p>
 *
 * <pre>
 * &#064;GET
 * &#064;Produces(&quot;text/plain&quot;)
 * &#064;Path(&quot;/forward/object&quot;)
 * public SomeObject forward(@Context InternalDispatcher dispatcher) {
 *     return (SomeObject) dispatcher.getEntity(&quot;/some-object&quot;);
 * }
 * </pre>
 * <p>
 * That previous snippet performs an internal request to /some-object and
 * returns the Object representation of the Resource that lives at
 * "/some-object".
 * </p>
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class InternalDispatcher {

    private static InternalDispatcher instance = new InternalDispatcher();

    // using a singleton so that that end users can override this behavior
    public static InternalDispatcher getInstance() {
        return instance;
    }

    public static void setInstance(InternalDispatcher instance) {
        InternalDispatcher.instance = instance;
    }

    public Object getEntity(String uri) {
        return getResponse(uri).getEntity();
    }

    public Response delete(String uri) {
        return getResponse(createRequest(uri, "DELETE"));
    }

    public Response putEntity(String uri, String contentType, Object entity) {
        return executeEntity("PUT", uri, contentType, entity);

    }

    public Response putEntity(String uri, Object entity) {
        return putEntity(uri, "*/*", entity);
    }

    public Response executeEntity(String method, String uri, String contentType, Object entity) {
        MockHttpRequest post = createRequest(uri, method);
        post.contentType(contentType);
        return getResponse(post, entity);
    }

    public Response postEntity(String uri, String contentType, Object entity) {
        return executeEntity("POST", uri, contentType, entity);
    }

    public Response postEntity(String uri, Object entity) {
        return postEntity(uri, "*/*", entity);
    }

    public Response getResponse(String uri) {
        return getResponse(createRequest(uri, "GET"));
    }

    public Response getResponse(MockHttpRequest request) {
        return getResponse(request, null);
    }

    public Response getResponse(MockHttpRequest request, Object entity) {
        try {
            Dispatcher dispatcher = ResteasyContext.getContextData(Dispatcher.class);
            if (dispatcher == null) {
                return null;
            }
            enhanceRequest(request);
            return dispatcher.internalInvocation(request, new MockHttpResponse(), entity);
        } finally {
        }

    }

    protected void enhanceRequest(MockHttpRequest request) {
        HttpRequest previousRequest = ResteasyContext.getContextData(HttpRequest.class);
        if (previousRequest != null) {
            request.getMutableHeaders().putAll(previousRequest.getHttpHeaders().getRequestHeaders());
        }
    }

    public static MockHttpRequest createRequest(String relativeUri, String verb) {
        UriInfo uriInfo = ResteasyContext.getContextData(UriInfo.class);

        URI baseUri = uriInfo.getBaseUri();
        URI absoluteUri = baseUri.resolve(parseRelativeUri(relativeUri));
        return MockHttpRequest.create(verb, absoluteUri, baseUri);
    }

    private static URI parseRelativeUri(String relativeUri) {
        if (relativeUri.startsWith("/")) {
            return URI.create(relativeUri.substring(1));
        }
        return URI.create(relativeUri);
    }
}
