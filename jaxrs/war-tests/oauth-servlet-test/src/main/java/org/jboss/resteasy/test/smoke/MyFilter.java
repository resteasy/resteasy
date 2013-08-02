package org.jboss.resteasy.test.smoke;

import org.jboss.resteasy.auth.oauth.OAuthFilter;
import org.jboss.resteasy.auth.oauth.OAuthToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MyFilter extends OAuthFilter {

    public static final String Consumer1Access1Principal = "admin-name";
    
    protected HttpServletRequest createSecurityContext(HttpServletRequest request, 
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer,
            OAuthToken accessToken) 
    {

        if (accessToken != null)
        {
            // permissions need to be converted into the actual roles
            // but here we assume permissions are the real roles
            
            final Set<String> roles = accessToken.getPermissions() != null 
                ? new HashSet<String>(Arrays.asList(accessToken.getPermissions())) 
                : new HashSet<String>();
            
            request = new HttpServletRequestWrapper(request){
                @Override
                public Principal getUserPrincipal(){
                    return new SimplePrincipal(Consumer1Access1Principal);
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
        return request;
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
