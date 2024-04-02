package org.jboss.resteasy.client.jaxrs.internal;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.util.HeaderHelper;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseContextImpl implements ClientResponseContext {
    protected final ClientResponse response;

    public ClientResponseContextImpl(final ClientResponse response) {
        this.response = response;
    }

    @Override
    public int getStatus() {
        return response.getStatus();
    }

    @Override
    public void setStatus(int code) {
        response.setStatus(code);
    }

    @Override
    public Response.StatusType getStatusInfo() {
        return response.getStatusInfo();
    }

    @Override
    public void setStatusInfo(Response.StatusType statusInfo) {
        response.setStatus(statusInfo.getStatusCode());
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public MultivaluedMap<String, String> getHeaders() {
        Object obj = response.getHeaders();
        return (MultivaluedMap<String, String>) obj;
    }

    @Override
    public Set<String> getAllowedMethods() {
        return response.getAllowedMethods();
    }

    @Override
    public Date getDate() {
        return response.getDate();
    }

    @Override
    public Locale getLanguage() {
        return response.getLanguage();
    }

    @Override
    public int getLength() {
        return response.getLength();
    }

    @Override
    public MediaType getMediaType() {
        return response.getMediaType();
    }

    @Override
    public Map<String, NewCookie> getCookies() {
        return response.getCookies();
    }

    @Override
    public EntityTag getEntityTag() {
        return response.getEntityTag();
    }

    @Override
    public Date getLastModified() {
        return response.getLastModified();
    }

    @Override
    public URI getLocation() {
        return response.getLocation();
    }

    @Override
    public Set<Link> getLinks() {
        return response.getLinks();
    }

    @Override
    public boolean hasLink(String relation) {
        return response.hasLink(relation);
    }

    @Override
    public Link getLink(String relation) {
        return response.getLink(relation);
    }

    @Override
    public Link.Builder getLinkBuilder(String relation) {
        return response.getLinkBuilder(relation);
    }

    @Override
    public boolean hasEntity() {
        return response.hasEntity();
    }

    @Override
    public InputStream getEntityStream() {
        return response.getEntityStream();
    }

    @Override
    public void setEntityStream(InputStream entityStream) {
        response.setInputStream(entityStream);
    }

    @Override
    public String getHeaderString(String name) {
        return response.getHeaderString(name);
    }

    @Override
    public boolean containsHeaderString(final String name, final Predicate<String> valuePredicate) {
        return containsHeaderString(name, ",", valuePredicate);
    }

    @Override
    public boolean containsHeaderString(final String name, final String valueSeparatorRegex,
            final Predicate<String> valuePredicate) {
        return HeaderHelper.containsHeaderString(getHeaderString(name), valueSeparatorRegex, valuePredicate);
    }

    // hack for MP exception mapping.  TODO revisit this implementation
    public ClientResponse getClientResponse() {
        return response;
    }
}
