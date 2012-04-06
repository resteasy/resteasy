package org.jboss.resteasy.examples.oauth.authenticator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.auth.oauth.OAuthValidator;


public class OAuthBasicAuthenticator extends AuthenticatorBase {

    private static final String INFO =
        "org.jboss.resteasy.examples.oauth.OAuthBasicAuthenticator/1.0";

    private static final Set<String> SUPPORTED_AUTH_METHODS = 
        new HashSet<String>(Arrays.asList("oauth", "basic", "oauth+basic", "basic+oauth"));
    
    private BasicAuthenticator ba = new BasicAuthenticator();

    /**
     * These DB connection properties are not used at the moment as a DB-aware 
     * OAuthProvider expects db.properties be available on the class path;
     * However, an OAuthProvider constructor accepting either Properties or Map
     * can be used when instantiating the provider and have these properties injected.
     *
     * This option can work given that it is easy to inject the configuration properties
     * into this Authenticator implementation but it is tricky to do for OAuthProvider
     * unless it is converted into a Catalina Realm which makes it all very complicated 
     * when we have Basic and OAuth - given that Basic and OAuth realms 
     * (i.e, databases of users and their passwords, etc) are unlikely to intersect or work
     * in the "or" combination.  
     */
    protected String driver;
    protected String url;
    protected String user;
    protected String password;
    
    private String oauthProviderName;
    
    private OAuthProvider oauthProvider;
    private OAuthValidator validator;
    
    private Realm originalRealm;
    
    public OAuthBasicAuthenticator() {
        super();
    }
    
    public String getInfo() {
        return INFO;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public String getUser()
    {
        return user;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setOauthProviderName(String oauthProviderName) {
        this.oauthProviderName = oauthProviderName;
    }

    public String getOauthProviderName() {
        return oauthProviderName;
    }

    @Override
    public void setContainer(Container container) {
        super.setContainer(container);
        ba.setContainer(container);
        originalRealm = container.getRealm();
    }
    
    @Override
    protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig config)
            throws IOException {
        
        String authMethod = config.getAuthMethod();
        if (!SUPPORTED_AUTH_METHODS.contains(authMethod.toLowerCase())) {
            throw new SecurityException("Unsupported auth method : " + authMethod);    
        }
        
        String authorization = request.getHeader("Authorization");
        if (authorization != null) 
        {
            context.setRealm(originalRealm);
            return ba.authenticate(request, response, config);
        } 
        else 
        {
            try {
                doAuthenticateOAuth(request, response);
            } catch (ServletException ex) {
                throw new IOException(ex);
            }
        }
        return false;
        
    }
    
    
    @Override
    public void start() throws LifecycleException {
        super.start();
        
        try {
            Class<?> providerClass = Class.forName(oauthProviderName);
            Constructor<?> constructor = providerClass.getConstructor(Map.class);
            Map<String, String> props = new HashMap<String, String>();
            props.put("db.driver", driver);
            props.put("db.url", url);
            props.put("db.username", user);
            props.put("db.password", password);
            oauthProvider = (OAuthProvider)constructor.newInstance(props); 
            validator = new OAuthValidator(oauthProvider);
        } catch (Exception ex) {
            throw new LifecycleException("In memory OAuth DB can not be created " + ex.getMessage());
        }
    }


    protected void doAuthenticateOAuth(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        OAuthMessage message = OAuthUtils.readMessage(request);
        try{

            message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
                    OAuth.OAUTH_SIGNATURE_METHOD,
                    OAuth.OAUTH_SIGNATURE,
                    OAuth.OAUTH_TIMESTAMP,
                    OAuth.OAUTH_NONCE);

            String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = oauthProvider.getConsumer(consumerKey);
        
            OAuthToken accessToken = null;
            String accessTokenString = message.getParameter(OAuth.OAUTH_TOKEN);
            
            if (accessTokenString != null) { 
                accessToken = oauthProvider.getAccessToken(consumer.getKey(), accessTokenString);
                OAuthUtils.validateRequestWithAccessToken(
                        request, message, accessToken, validator, consumer);
            } else {
                OAuthUtils.validateRequestWithoutAccessToken(
                        request, message, validator, consumer);
            }
            
            createPrincipalAndRoles(request, consumer, accessToken);
            getNext().invoke((Request)request, (Response)response);
            
        } catch (OAuthException x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), x.getHttpCode(), oauthProvider);
        } catch (OAuthProblemException x) {
            OAuthUtils.makeErrorResponse(response, x.getProblem(), OAuthUtils.getHttpCode(x), oauthProvider);
        } catch (Exception x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, oauthProvider);
        }
        
    }

    protected void createPrincipalAndRoles(HttpServletRequest request, 
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer,
            OAuthToken accessToken) 
    {
        
        Set<String> roles = oauthProvider.convertPermissionsToRoles(accessToken.getPermissions());
        Realm realm = new OAuthRealm(roles);
        context.setRealm(realm);
        
        final Principal principal = new GenericPrincipal(realm, consumer.getKey(), "", new ArrayList<String>(roles));
        ((Request)request).setUserPrincipal(principal);
        ((Request)request).setAuthType("OAuth");
    }
    
    private static class OAuthRealm extends RealmBase {

        private Set<String> roles;
        
        public OAuthRealm(Set<String> roles) {
            this.roles = roles;
        }
        
        @Override
        protected String getName() {
            return "OAuthRealm";
        }

        @Override
        protected String getPassword(String username) {
            return "";
        }

        @Override
        protected Principal getPrincipal(String username) {
            return new GenericPrincipal(this, username, "", null, null);
        }
        
        @Override
        public boolean hasResourcePermission(Request request, Response response,  
             SecurityConstraint[] constraints, Context context) {  
           return true;  
        } 
        
        @Override
        public boolean hasRole(Principal principal, String role) {  
           return roles.contains(role);  
        }
    }
}
