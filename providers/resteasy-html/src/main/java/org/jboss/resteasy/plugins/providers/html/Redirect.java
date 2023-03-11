package org.jboss.resteasy.plugins.providers.html;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.resteasy.util.HttpHeaderNames;

public class Redirect implements Renderable {

    private URI path;

    public Redirect(final URI path) {
        this.path = path;
    }

    /**
     * @param path
     *             must be a valid URI
     */
    public Redirect(final String path) {
        try {
            this.path = new URI(path);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    public URI getPath() {
        return this.path;
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException,
            WebApplicationException {
        response.setStatus(Status.SEE_OTHER.getStatusCode());
        response.setHeader(HttpHeaderNames.LOCATION, this.path.toString());
    }

}
