package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class ObjectWriterModifierMultipleFilter implements Filter {
    private static ObjectFilterModifierMultiple modifier = new ObjectFilterModifierMultiple();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ObjectWriterInjector.set(modifier);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
