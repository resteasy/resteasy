package org.jboss.resteasy.spi;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

/**
 * Bridge interface between the base Resteasy JAX-RS implementation and the actual HTTP transport (i.e. a servlet container)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpRequest {
    HttpHeaders getHttpHeaders();

    MultivaluedMap<String, String> getMutableHeaders();

    InputStream getInputStream();

    /**
     * If you are using a servlet container, this will *NOT* override the HttpServletRequest.getInputStream().
     * It will only override it for the resteasy HttpRequest
     *
     * @param stream input stream
     */
    void setInputStream(InputStream stream);

    /**
     * This method *MUST* always return the same instance.
     *
     * @return uri info
     */
    UriInfo getUri();

    String getHttpMethod();

    void setHttpMethod(String method);

    /**
     * Updates the object returned by {@link #getUri()}.
     *
     * @param requestUri request uri
     */
    void setRequestUri(URI requestUri) throws IllegalStateException;

    /**
     * Updates the object returned by {@link #getUri()}.
     *
     * @param baseUri    base uri
     * @param requestUri request uri
     */
    void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException;

    /**
     * application/x-www-form-urlencoded parameters
     * <p>
     * This is here because @FormParam needs it and for when there are servlet filters that eat up the input stream
     *
     * @return null if no parameters, this is encoded map
     */
    MultivaluedMap<String, String> getFormParameters();

    MultivaluedMap<String, String> getDecodedFormParameters();

    /**
     * Returns for {@linkplain EntityPart entity parts} for a {@code multipart/form-data} request.
     *
     * @return the entity parts or an empty list
     */
    default List<EntityPart> getFormEntityParts() {
        return Collections.emptyList();
    }

    /**
     * Returns the optional {@linkplain EntityPart entity part} for a {@code multipart/form-data} request.
     *
     * @param name the name of the entity part
     *
     * @return an optional entity part for the name
     */
    default Optional<EntityPart> getFormEntityPart(final String name) {
        return getFormEntityParts()
                .stream()
                .filter((e) -> name.equals(e.getName()))
                .findFirst();
    }

    /**
     * Were form parameters read before marshalling to body?
     *
     * @return
     */
    boolean formParametersRead();

    /**
     * Map of contextual data. Similar to HttpServletRequest attributes
     *
     * @param attribute attribute name
     * @return attribute
     */
    Object getAttribute(String attribute);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);

    Enumeration<String> getAttributeNames();

    ResteasyAsynchronousContext getAsyncContext();

    boolean isInitial();

    void forward(String path);

    boolean wasForwarded();

    /**
     * Returns the Internet Protocol (IP) address of the client
     * or last proxy that sent the request.
     *
     * @return a <code>String</code> containing the
     *         IP address of the client that sent the request
     */
    String getRemoteAddress();

    /**
     * Returns the fully qualified name of the client
     * or the last proxy that sent the request.
     * If the engine cannot or chooses not to resolve the hostname
     * (to improve performance), this method returns the dotted-string form of
     * the IP address.
     *
     * @return a <code>String</code> containing the fully
     *         qualified name of the client
     */
    String getRemoteHost();
}
