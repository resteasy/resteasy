package org.jboss.resteasy.examples.oauth;

import org.apache.commons.codec.binary.Base64;
import org.jboss.security.AuthenticationManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class OAuthBasicAuthenticationFilter extends OAuthDBFilter {

    private AuthenticationManager am;
    public OAuthBasicAuthenticationFilter() 
    {
        try
        {
           Context ctx = new InitialContext();
           Object obj = ctx.lookup("java:/comp/env/security/securityMgr");
           am = (AuthenticationManager) obj;
        }
        catch (NamingException ne)
        {
           throw new SecurityException("Unable to lookup AuthenticationManager using JNDI");
        }
    }
    
    @Override
    protected void _doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Basic"))
        {
            String base64Value = header.substring(6);
            Base64 base64 = new Base64();
            String decoded = new String(base64.decode(base64Value.getBytes()));
            String[] pair = decoded.split(":");
            String username = pair[0];
            String password = pair[1];
            request = createSecurityContext(request, username, password);
            filterChain.doFilter(request, response);
        } else 
        {
            super._doFilter(request, response, filterChain);    
        }
    }
    
    private HttpServletRequest createSecurityContext(HttpServletRequest request,
                                                     String username,
                                                     String password) 
    {
        final Principal principal = new SimplePrincipal(username);
        Subject subject = new Subject();
 
        if (am.isValid(principal, password, subject) == false)
        {
            String msg = "Authentication failed, principal=" + principal.getName();
            throw new SecurityException(msg);
        }
        
        final Set<String> roles = getRoles(subject);
        
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
    
    private Set<String> getRoles(Subject subject) {
        Set<String> roles = new HashSet<String>();
        for (Principal principal : subject.getPrincipals()) {
            if (principal instanceof Group) { 
                for (Enumeration<? extends Principal> members = ((Group)principal).members();
                     members.hasMoreElements();) {
                     roles.add(members.nextElement().getName());
                }
            }
        }
        return roles;
    }
}