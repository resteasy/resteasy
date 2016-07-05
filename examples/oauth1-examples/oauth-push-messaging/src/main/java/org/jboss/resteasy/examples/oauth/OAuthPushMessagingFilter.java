package org.jboss.resteasy.examples.oauth;

import org.jboss.resteasy.auth.oauth.OAuthConsumer;
import org.jboss.resteasy.auth.oauth.OAuthFilter;
import org.jboss.resteasy.auth.oauth.OAuthToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;
import java.util.Set;

public class OAuthPushMessagingFilter extends OAuthFilter {

    public OAuthPushMessagingFilter() 
    {
        
    }
    
    protected HttpServletRequest createSecurityContext(HttpServletRequest request, 
            OAuthConsumer consumer, OAuthToken accessToken) 
    {
        final Principal principal = new SimplePrincipal(consumer.getKey());
        final Set<String> roles = getRoles(consumer);
        
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
    
        
    private Set<String> getRoles(OAuthConsumer consumer) {
        
        return getProvider().convertPermissionsToRoles(consumer.getPermissions());
        
    }
    
}