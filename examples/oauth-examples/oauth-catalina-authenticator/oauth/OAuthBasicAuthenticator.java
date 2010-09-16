package org.jboss.resteasy.examples.oauth;

import java.io.IOException;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;


public class OAuthBasicAuthenticator extends AuthenticatorBase {

    private static final String INFO =
        "org.jboss.resteasy.examples.oauth.OAuthBasicAuthenticator/1.0";
    
    private static BasicAuthenticator ba = new BasicAuthenticator();
    
    public String getInfo() {
        return INFO;
    }

    
    @Override
    protected boolean authenticate(Request request, Response response, LoginConfig config)
            throws IOException {
        MessageBytes authorization = 
            request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        if (authorization != null) {
            authorization.toBytes();
            ByteChunk authorizationBC = authorization.getByteChunk();
            if (authorizationBC.startsWithIgnoreCase("basic ", 0)) {
                return bc.authenticate(request, response, config);
            } 
            else if (authorizationBC.startsWithIgnoreCase("oauth ", 0)) {
                return authenticateOAuth(request, response, config);
            }
        }

        return false;
    }
    
    
    protected boolean authenticateOAuth(Request request, Response response, LoginConfig config)
            throws IOException {
//        Principal principal = context.getRealm().authenticate(username, password);
//        if (principal != null) {
//            register(request, response, principal, Constants.BASIC_METHOD,
//                     username, password);
//            return true;
//        }

        // do what OAuth filter does : get the oauth data from the Authoriz header or from the GET/POST
        // parameters, validate them, create Principal and roles
        
        return false;
    }

}
