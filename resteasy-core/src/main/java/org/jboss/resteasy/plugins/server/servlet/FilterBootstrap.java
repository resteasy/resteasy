package org.jboss.resteasy.plugins.server.servlet;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FilterBootstrap extends ListenerBootstrap {
    private FilterConfig config;

    public FilterBootstrap(final FilterConfig config) {
        super(config.getServletContext());
        this.config = config;
    }

    @Override
    public ResteasyDeployment createDeployment() {
        ResteasyDeployment deployment = super.createDeployment();
        deployment.getDefaultContextObjects().put(FilterConfig.class, config);
        deployment.getDefaultContextObjects().put(ServletContext.class, config.getServletContext());
        return deployment;
    }

    @Override
    public String getInitParameter(String name) {
        String value = config.getInitParameter(name);
        if (value == null) {
            value = super.getInitParameter(name);
        }
        return value;
    }

    @Override
    public Set<String> getParameterNames() {
        Set<String> set = super.getInitParameterNames();
        Enumeration<String> en = config.getInitParameterNames();
        while (en.hasMoreElements())
            set.add(en.nextElement());
        return set;
    }

    @Override
    public Set<String> getInitParameterNames() {
        Set<String> set = new HashSet<String>();
        Enumeration<String> en = config.getInitParameterNames();
        while (en.hasMoreElements())
            set.add(en.nextElement());
        return set;
    }
}
