/**
 * Created on 2007-4-14 00:54:50
 */
package org.jboss.resteasy.examples.openid;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.DirectError;
import org.openid4java.message.Message;
import org.openid4java.message.ParameterList;
import org.openid4java.server.InMemoryServerAssociationStore;
import org.openid4java.server.ServerManager;

public class ProviderServlet extends javax.servlet.http.HttpServlet {

    private static final String OP_ENDPOINT_URL;
    static {
        Properties props = new Properties();
        try {
            props.load(ProviderServlet.class.getResourceAsStream("/openid.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("oauth.properties resource is not available");
        }
        OP_ENDPOINT_URL = props.getProperty("openid.provider.op.endpoint");
        
    }
	
	private ServletContext context;
	private ServerManager manager;
	
	// this realms have to be user-specific
	private ConcurrentHashMap<String, Set<String>> trustedRealmsMap = 
	    new ConcurrentHashMap<String, Set<String>>();
	private boolean strictRealmCheck;
	
	
	/**
	 * {@inheritDoc}
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		context = config.getServletContext();

		this.manager = new ServerManager();
		manager.setOPEndpointUrl(OP_ENDPOINT_URL);
        manager.getRealmVerifier().setEnforceRpId(false);
        manager.setSharedAssociations(new InMemoryServerAssociationStore());
        manager.setPrivateAssociations(new InMemoryServerAssociationStore());
		
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException 
	{
	    
		if ("HEAD".equals(req.getMethod())) 
		{
			resp.setStatus(200);
			return;
		}
		String acceptValue = req.getHeader("Accept");
		if (acceptValue != null && acceptValue.contains("application/xrds+xml")) 
		{
			InputStream is = context.getResourceAsStream("/WEB-INF/discovery.xml");
			byte[] buffer =  new byte[is.available()];
			is.read(buffer, 0, is.available());
			resp.setContentType("application/xrds+xml");
			resp.getOutputStream().write(buffer);
			resp.getOutputStream().flush();
			return;
		}
		doPost(req, resp);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String pathInfo = req.getPathInfo();
		
		if (pathInfo.equals("/trusted_realms"))
		{
			serveRealmsRegistration(req, resp);
		}
		else 
		{
			serveAuthenticationRequest(req, resp);
		}
    }

	private void serveAuthenticationRequest(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException
	{
		// extract the parameters from the request
        ParameterList request = new ParameterList(req.getParameterMap());

        String mode = request.hasParameter("openid.mode") ?
                request.getParameterValue("openid.mode") : null;

        Message response;
        String responseText;

        if ("associate".equals(mode))
        {
            // --- process an association request ---
            response = manager.associationResponse(request);
            responseText = response.keyValueFormEncoding();
        }
        else if ("checkid_immediate".equals(mode))
        {
        	String userSelectedClaimedId = (String) request.getParameter("openid.claimed_id").getValue();
        	
            String realm = (String) request.getParameter("openid.realm").getValue();
            
            if (!isTrustedRealm(realm, userSelectedClaimedId)) {
                response = DirectError.createDirectError("checkid_immediate is not supported");
                responseText = response.keyValueFormEncoding();
                directResponse(resp, responseText);
                return;
            }
        	
            // --- process an authentication request ---
            AuthRequest authReq = null;
            try {
                authReq = AuthRequest.createAuthRequest(request, manager.getRealmVerifier());
            } catch (Exception ex) {
            	throw new ServletException(ex);
            }

            String opLocalId = null;
            // if the user chose a different claimed_id than the one in request
            if (userSelectedClaimedId != null &&
                userSelectedClaimedId.equals(authReq.getClaimed()))
            {
                //opLocalId = lookupLocalId(userSelectedClaimedId);
            }
            
            response = manager.authResponse(request,
                    opLocalId,
                    userSelectedClaimedId,
                    true,
                    false); // Sign after we added extensions.

            if (response instanceof DirectError)
                responseText = response.keyValueFormEncoding();
            else
            {
                // Sign the auth success message.
                // This is required as AuthSuccess.buildSignedList has a `todo' tag now.
            	try {
                    manager.sign((AuthSuccess) response);
            	} catch (Exception ex) {
            		throw new ServletException(ex);
            	}
                responseText = response.keyValueFormEncoding();
            }
        } 
        else
        {
        	// unsupported mode
            // --- error response ---
            response = DirectError.createDirectError("Unknown request");
            responseText = response.keyValueFormEncoding();
        }

        directResponse(resp, responseText);
	}

	private void serveRealmsRegistration(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
		String[] values = req.getParameterValues("xopenid.realm");
		if (values != null && values.length > 0)
		{
		    Principal principal = req.getUserPrincipal();
		    if (principal == null) 
		    {
		        resp.setStatus(401);
		        return;
		    }
			trustedRealmsMap.putIfAbsent(principal.getName(),
			        new HashSet<String>(Arrays.asList(values)));
		}
    }
	
    private String directResponse(HttpServletResponse httpResp, String response)
            throws IOException
    {
        ServletOutputStream os = httpResp.getOutputStream();
        os.write(response.getBytes());
        os.close();

        return null;
    }

    private boolean isTrustedRealm(String realm, String openId) 
    {
        int index = openId.lastIndexOf("/");
        String name = index != -1 ? openId.substring(index + 1) : openId;
     
        Set<String> trustedRealms = trustedRealmsMap.get(name);
        if (trustedRealms == null) 
        {
            return false;
        }
        
        if (strictRealmCheck) 
        {
            return trustedRealms.contains(realm);    
        }
        else
        {
            for (String trustedRealm : trustedRealms)
            {
                if (realm.startsWith(trustedRealm))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
