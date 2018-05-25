package org.jboss.resteasy.auth.oauth;

import net.oauth.OAuth;
import net.oauth.OAuth.Parameter;
import net.oauth.OAuth.Problems;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.jboss.resteasy.auth.oauth.i18n.LogMessages;
import org.jboss.resteasy.auth.oauth.i18n.Messages;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OAuthUtils {
	
	/**
	 * HTTP Authorization header
	 */
	public static final String AUTHORIZATION_HEADER = "Authorization";

	/**
	 * HTTP WWW-Authenticate header
	 */
	public static final String AUTHENTICATE_HEADER = "WWW-Authenticate";
	
	/**
	 * OAuth Verifier parameter
	 */
	public static final String OAUTH_VERIFIER_PARAM = "oauth_verifier";

	/**
	 * OAuth Callback Confirmed parameter
	 */
	public static final String OAUTH_CALLBACK_CONFIRMED_PARAM = "oauth_callback_confirmed";
	
	/**
	 * Name of the OAuthValidator Servlet Context Attribute name.
	 */
	private static final String ATTR_OAUTH_VALIDATOR = OAuthValidator.class.getName();

	/**
	 * Name of the OAuthProvider Servlet Context Attribute name.
	 */
	private static final String ATTR_OAUTH_PROVIDER = OAuthProvider.class.getName();

	/**
	 * Encodes the given value for use in an OAuth parameter.
	 * @param value string to encode
	 * @return encoded string
	 */
	public static String encodeForOAuth(String value){
		try {
			return URLUtils.encodePart(value, StandardCharsets.UTF_8.name(), URLUtils.UNRESERVED);
		} catch (UnsupportedEncodingException e) {
			// this encoding is specified in the JDK
		   throw new RuntimeException(Messages.MESSAGES.utf8EncodingShouldBeSupported(), e);
		}
	}
	
	/**
	 * Sends a list of OAuth parameters in the body of the given Http Servlet Response
	 * @param resp http response
	 * @param params a list of {@literal <name, value>} parameters
	 * @throws IOException if I/O error occurred
	 */
	public static void sendValues(HttpServletResponse resp, String... params) throws IOException {
		PrintWriter writer = resp.getWriter();
		if((params.length % 2) != 0)
		   throw new IllegalArgumentException(Messages.MESSAGES.argumentsShouldBeNameValue());
		for(int i=0;i<params.length;i+=2){
			if(i > 0)
				writer.append('&');
			writer.append(encodeForOAuth(params[i]));
			writer.append('=');
			writer.append(encodeForOAuth(params[i+1]));
		}
	}

	/**
	 * Reads an OAuthMessage from an HTTP Servlet Request. Uses the Authorization header, GET and POST parameters.
	 * @param req http request
	 * @return {@link OAuthMessage}
	 */
	public static OAuthMessage readMessage(HttpServletRequest req) {
		String authorizationHeader = req.getHeader(AUTHORIZATION_HEADER);
		List<OAuth.Parameter> parameters = new ArrayList<OAuth.Parameter>();
		
		// first read the Authorization header
		if(authorizationHeader != null){
			for(Parameter param : OAuthMessage.decodeAuthorization(authorizationHeader)){
				if(!"realm".equalsIgnoreCase(param.getValue()))
					parameters.add(param);
			}
		}
		// Read all parameters from either POST or the query String
		@SuppressWarnings("unchecked")
		List<String> parameterNames = Collections.<String>list(req.getParameterNames());
		for(String parameterName : parameterNames){
			for(String value : req.getParameterValues(parameterName)){
			   LogMessages.LOGGER.debug(Messages.MESSAGES.addingParameter(parameterName, value));
				parameters.add(new OAuth.Parameter(parameterName, value));
			}
		}
		
		return new OAuthMessage(req.getMethod(), req.getRequestURL().toString(), parameters);

	}

	/**
	 * Sends an error to the OAuth Consumer.
	 * @param resp http response
	 * @param message response message
	 * @param httpCode response status code
	 * @param provider {@link OAuthProvider}
	 * @throws IOException if I/O error occurred
	 */
	public static void makeErrorResponse(HttpServletResponse resp, String message, int httpCode, OAuthProvider provider) throws IOException{
	   LogMessages.LOGGER.debug(Messages.MESSAGES.errorHttpCode(httpCode, message));
		resp.getWriter().append(message);
		resp.setStatus(httpCode);
		String headerValue = "OAuth";
		if (provider.getRealm() != null && provider.getRealm().length() > 0) {
		    headerValue += (" realm=\"" + provider.getRealm() + "\"");
		}
		resp.setHeader(AUTHENTICATE_HEADER, headerValue);
	}

	/**
	 * Parse an OAuth timestamp.
	 * @param timestampString timestamp
	 * @return OAuth timestamp
	 * @throws OAuthException if error occurred
	 */
	public static long parseTimestamp(String timestampString) throws OAuthException {
		try{
			long timestamp = Long.parseLong(timestampString);
			if(timestamp > 0)
				return timestamp;
		}catch(NumberFormatException x){
			// fallback
		}
		throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, Messages.MESSAGES.invalidTimestampString(timestampString));
	}

	/**
	 * Finds the HTTP status code from the given exception.
	 * @param x exception
	 * @return http status code
	 */
	public static int getHttpCode(OAuthProblemException x){
		Integer httpCode = Problems.TO_HTTP_CODE.get(x.getProblem());
		if(httpCode != null)
			return httpCode;
		return HttpURLConnection.HTTP_INTERNAL_ERROR;

	}
	
	/**
	 * Loads the OAuthProvider as specified in the Servlet Context parameters, and caches it in the Servlet Context attributes for reuse.
	 * @param context servlet context
	 * @return {@link OAuthProvider}
	 * @throws ServletException servlet exception
	 */
	public static OAuthProvider getOAuthProvider(ServletContext context) throws ServletException {
		OAuthProvider provider = (OAuthProvider) context.getAttribute(ATTR_OAUTH_PROVIDER);
		if(provider != null)
			return provider;
		
		String providerClassName = context.getInitParameter(OAuthServlet.PARAM_PROVIDER_CLASS);
		if(providerClassName == null)
		   throw new ServletException(OAuthServlet.PARAM_PROVIDER_CLASS+Messages.MESSAGES.parameterRequired());
		try {
		   LogMessages.LOGGER.info(Messages.MESSAGES.loadingOAuthProvider(providerClassName));
			Class<?> providerClass = Class.forName(providerClassName);
			if(!OAuthProvider.class.isAssignableFrom(providerClass))
			   throw new ServletException(OAuthServlet.PARAM_PROVIDER_CLASS+Messages.MESSAGES.classMustBeInstanceOAuthProvider(providerClassName));
			provider = new OAuthProviderChecker((OAuthProvider) providerClass.newInstance());
			context.setAttribute(ATTR_OAUTH_PROVIDER, provider);
			return provider;
		} catch (ClassNotFoundException e) {
	       throw new ServletException(OAuthServlet.PARAM_PROVIDER_CLASS+Messages.MESSAGES.classNotFound(providerClassName));
		} catch (Exception e) {
		   throw new ServletException(OAuthServlet.PARAM_PROVIDER_CLASS+Messages.MESSAGES.classCouldNotBeInstantiated(providerClassName), e);
		}
	}

	/**
	 * Creates an OAuthValidator, and caches it in the Servlet Context attributes for reuse.
	 * @param context servlet context
	 * @param provider {@link OAuthProvider}
	 * @return validator
	 */
	public static OAuthValidator getValidator(ServletContext context,
			OAuthProvider provider) {
		OAuthValidator validator = (OAuthValidator) context.getAttribute(ATTR_OAUTH_VALIDATOR);
		if(validator != null)
			return validator;
		
		validator = new OAuthValidator(provider);
		context.setAttribute(ATTR_OAUTH_VALIDATOR, validator);
		return validator;
	}
	
	public static void validateRequestWithAccessToken(
	        HttpServletRequest request,
            OAuthMessage message,
            OAuthToken accessToken,
            OAuthValidator validator,
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer) throws Exception {
	    
        OAuthConsumer _consumer = new OAuthConsumer(null, consumer.getKey(), accessToken.getConsumer().getSecret(), null);
        OAuthAccessor accessor = new OAuthAccessor(_consumer);
        accessor.accessToken = accessToken.getToken();
        accessor.tokenSecret = accessToken.getSecret();
        
        // validate the message
        validator.validateMessage(message, accessor, accessToken);
        if (!OAuthUtils.validateUriScopes(request.getRequestURL().toString(), accessToken.getScopes())) {
           throw new OAuthException(HttpURLConnection.HTTP_BAD_REQUEST, Messages.MESSAGES.wrongURIScope());
        }
	}
	
	/**
	 * Validates if a given request is a valid 2-leg oAuth request.
	 * @param request http request
	 * @param message message
	 * @param validator validator
	 * @param consumer consumer
	 * @throws Exception if error occurred
	 */
	public static void validateRequestWithoutAccessToken(
	        HttpServletRequest request,
	        OAuthMessage message,
	        OAuthValidator validator,
	        org.jboss.resteasy.auth.oauth.OAuthConsumer consumer) throws Exception 
	{
	    
	    String[] scopes = consumer.getScopes();
        if (scopes == null || !validateUriScopes(request.getRequestURL().toString(), scopes)) {
           throw new OAuthException(HttpURLConnection.HTTP_BAD_REQUEST, Messages.MESSAGES.wrongURIScope());
        }
        // build some info for verification
        OAuthConsumer _consumer = new OAuthConsumer(null, consumer.getKey(), consumer.getSecret(), null);
        OAuthAccessor accessor = new OAuthAccessor(_consumer);
        // validate the message
        validator.validateMessage(message, accessor, null);
	}
	
	/**
	 * Validates if a current request URI matches URI provided by the consumer at the
	 * registration time or during the request token validation request.
	 * @param requestURI request uri
	 * @param scopes array of scopes
	 * @return true if scope is not defined or uri starts with scope, false otherwise
	 */
	public static boolean validateUriScopes(String requestURI, String[] scopes) {
        if (scopes == null) {
            return true;
        }
        for (String scope : scopes) {
            if (requestURI.startsWith(scope)) {
                return true;
            }
        }
        return false; 
    }
}
