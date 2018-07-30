package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyObjectWriterInjector;

public class ObjectWriterModifierConditionalFilter implements Filter {
    private static ObjectFilterModifierConditional modifier = new ObjectFilterModifierConditional();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ResteasyObjectWriterInjector.set(Thread.currentThread().getContextClassLoader(), modifier);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
