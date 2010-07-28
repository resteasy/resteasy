package org.jboss.resteasy.examples.oauth;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jboss.resteasy.auth.oauth.OAuthFilter;
import org.jboss.resteasy.auth.oauth.OAuthToken;

public class OAuthDBFilter extends OAuthFilter {

    private static Connection conn;
    static {
        Properties props = new Properties();
        try {
            props.load(OAuthDBFilter.class.getResourceAsStream("/db.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("db.properties resource is not available");
        }
        String driver = props.getProperty("db.driver");
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            throw new RuntimeException("In memory OAuth DB can not be created " + ex.getMessage());
        }
    }
    
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
        final Set<String> roles = getRoles(consumer.getKey());
        roles.addAll(convertPermissionsToRoles(accessToken.getPermissions()[0]));
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
    
        
    private Set<String> getRoles(String consumerKey) {
        Set<String> roles = new HashSet<String>();    
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT roles FROM consumers WHERE"
                    + " key = '" + consumerKey + "'");
            if (rs.next()) {
                String rolesValues = rs.getString("roles");
                roles.add(rolesValues);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("No roles exist for consumer key " + consumerKey);
        }
        return roles;
    }
    
    private Set<String> convertPermissionsToRoles(String permissions) {
        Set<String> roles = new HashSet<String>();
        // get the default roles which may've been allocated to a consumer
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT role FROM permissions WHERE"
                    + " permission='" + permissions + "'");
            if (rs.next()) {
                String rolesValues = rs.getString("role");
                roles.add(rolesValues);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("No role exists for permission " + permissions);
        }
        return roles;
    }
}