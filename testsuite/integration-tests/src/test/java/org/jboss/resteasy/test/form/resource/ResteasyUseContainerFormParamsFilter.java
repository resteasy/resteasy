package org.jboss.resteasy.test.form.resource;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Map;

@WebFilter("/*")
public class ResteasyUseContainerFormParamsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Map<String, String[]> theMap = request.getParameterMap();
        chain.doFilter(request, response);
    }
}
