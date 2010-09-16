package org.jboss.resteasy.examples.oauth;

import java.security.Principal;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jboss.resteasy.auth.oauth.OAuthFilter;
import org.jboss.resteasy.auth.oauth.OAuthToken;

public class OAuthDBFilter extends OAuthFilter {

    public OAuthDBFilter() 
    {
        
    }
    
    protected HttpServletRequest createSecurityContext(HttpServletRequest request, 
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer,
            OAuthToken accessToken) 
    {
        // Alternatively we can have an alias associated with a given key
        // Example: www.messageing.service : kermit
        final Principal principal = new SimplePrincipal(consumer.getKey());
        final Set<String> roles = getProvider().convertPermissionsToRoles(accessToken.getPermissions());
        return new HttpServletRequestWrapper(request){
            @Override
            public Principal getUserPrincipal(){
                return principal;
            }
            @Override
            public boolean isUserInRole(String role){
                return roles.contains(role);
            }
            @Override
            public String getAuthType(){
                return OAUTH_AUTH_METHOD;
            }
        };
    }
    
    private static class SimplePrincipal implements Principal
    {
        private String name;
        
        public SimplePrincipal(String name) 
        {
            this.name = name;    
        }
        
        public String getName() {
            return name;
        }
        
    }
    
    
}