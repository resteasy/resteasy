package org.jboss.resteasy.spi;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ValueInjector {
    /**
     * Inject outside the context of an HTTP request. For instance, a singleton may have proxiable and injectable
     * jax-rs objects like Request, UriInfo, or HttpHeaders.
     *
     * @param unwrapAsync unwrap async
     * @return object
     */
    Object inject(boolean unwrapAsync);

    /**
     * Inject inside the context of an HTTP request.
     *
     * @param request     http request
     * @param response    http response
     * @param unwrapAsync unwrap async
     * @return object
     */
    Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync);
}
