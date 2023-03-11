package org.jboss.resteasy.plugins.server.sun.http;

import java.util.Set;

import org.jboss.resteasy.spi.ResteasyConfiguration;

import com.sun.net.httpserver.HttpContext;

/**
 * ResteasyConfiguration adapter for HttpContext attributes
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpContextResteasyConfiguration implements ResteasyConfiguration {
    protected HttpContext context;

    public HttpContextResteasyConfiguration(final HttpContext context) {
        this.context = context;
    }

    @Override
    public String getParameter(String name) {
        Object val = context.getAttributes().get(name);
        if (val == null)
            return null;
        return val.toString();
    }

    @Override
    public Set<String> getParameterNames() {
        return context.getAttributes().keySet();
    }

    @Override
    public String getInitParameter(String name) {
        return getParameter(name);
    }

    @Override
    public Set<String> getInitParameterNames() {
        return getParameterNames();
    }
}
