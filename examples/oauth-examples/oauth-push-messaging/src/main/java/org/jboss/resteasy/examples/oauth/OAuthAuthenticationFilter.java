package org.jboss.resteasy.examples.oauth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthAuthenticationFilter extends OAuthPushMessagingFilter {

    public OAuthAuthenticationFilter() 
    {
    }
    
    @Override
    protected void _doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        if ("GET".equals(request.getMethod()))
        {
            filterChain.doFilter(request, response);
        } else 
        {
            super._doFilter(request, response, filterChain);    
        }
    }
    
}