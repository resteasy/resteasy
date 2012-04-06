package org.jboss.resteasy.examples.oauth;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.openid4java.association.AssociationSessionType;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;


public class OpenIdFilter extends OAuthPushMessagingFilter {

    private final static Map<String, String> ROLES;
    
    static 
    {
        ROLES = new HashMap<String, String>();
        ROLES.put("admin", "JBossAdmin");
    }

    private ConsumerManager manager;
    
    public OpenIdFilter() 
    {
    }
    
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        try 
        {
            this.manager = new ConsumerManager();
        } catch (ConsumerException ex) {
            throw new ServletException(ex);
        }
        manager.setAssociations(new InMemoryConsumerAssociationStore());
        manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
        manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
        manager.setImmediateAuth(true);
    }

    public void destroy() {
        super.destroy();
    }
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        _doFilter((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
    }
    
    protected void _doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
    
        // this approach is not 100% safe : we can imagine a MessageService
        // attempting to bypass an OAuth check by presenting its OpenId when trying
        // to push (unwanted) messages to the receiver; this filter may be additionally
        // configured with a list of URI for which OpenId is not supported
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("OpenId"))
        {
            String openId = header.substring(6).trim();
            authenticateUser(request, response, openId);
            request = createSecurityContext(request, openId);
            filterChain.doFilter(request, response);    
        } 
        else 
        {
            super._doFilter(request, response, filterChain);
        }
        
    }
    
    private void authenticateUser(HttpServletRequest httpReq, HttpServletResponse httpResp, 
            String openId) throws ServletException, IOException
    {
        try {
            // perform discovery on the user-supplied identifier
            List discoveries = manager.discover(openId);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);

            // store the discovery information in the user's session
            httpReq.getSession().setAttribute("openid-disc", discovered);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = null;

            String returnToUrl = httpReq.getRequestURL().toString();
            authReq = manager.authenticate(discovered, returnToUrl);
            String destinationUrl = authReq.getDestinationUrl(true);
            
            ClientRequest req = new ClientRequest(destinationUrl);
            ClientResponse<String> resp = null;
            ParameterList response = null;
            try {
               resp = req.get(String.class);
               String body = resp.getEntity();
               String[] paramValues = body.split("\n");
               Map<String, String> paramsMap = new HashMap<String, String>();
               for (String paramValue : paramValues) {
                   String theRealValue = paramValue.trim();
                   if (theRealValue.isEmpty()) {
                       continue;
                   }
                   int index = theRealValue.indexOf(":");
                   String key = theRealValue.substring(0, index);
                   String value = theRealValue.substring(index + 1);
                   paramsMap.put(key, value);
               }
               response = new ParameterList(paramsMap);
            } finally {
               resp.releaseConnection();
            }

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = 
                manager.verify(returnToUrl.toString(), response, discovered);

            // examine the verification result and extract the verified
            // identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess = (AuthSuccess) verification
                        .getAuthResponse();
                if (!openId.equals(authSuccess.getIdentity())) 
                {
                    throw new ServletException("Invalid Openid");
                }
            }
             
        } catch (Exception e) {
            // present error to the user
            throw new ServletException(e);
        }

        
    }
    
    protected HttpServletRequest createSecurityContext(HttpServletRequest request, 
                                                       String openId) 
    {
        int index = openId.lastIndexOf("/");
        String name = index != -1 ? openId.substring(index + 1) : openId;
        
        final Principal principal = new SimplePrincipal(name);
        final Set<String> roles = getRoles(name);
        
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
                return "OpenId";
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
    
        
    private Set<String> getRoles(String name) {
        Set<String> roles = new HashSet<String>();
        String role = ROLES.get(name);
        roles.add(role);
        return roles;
    }
    
}