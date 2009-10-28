package org.jboss.resteasy.auth.oauth;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth Http Servlet that handles Request Token creation and exchange for Access Tokens.
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class OAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 3083924242786185155L;

	private final static Logger logger = LoggerFactory.getLogger(OAuthServlet.class);

	/**
	 * Servlet context parameter name for the Request Token distribution URL
	 */
	final static String PARAM_REQUEST_TOKEN_URL = "oauth.provider.tokens.request";

	/**
	 * Servlet context parameter name for the Request Token echange URL
	 */
	final static String PARAM_ACCESS_TOKEN_URL = "oauth.provider.tokens.access";
	
	/**
	 * Servlet context parameter name for the OAuthProvider class name
	 */
	final static String PARAM_PROVIDER_CLASS = "oauth.provider.provider-class";
	
	private String requestTokenURL, accessTokenURL;
	private OAuthProvider provider;
	private OAuthValidator validator;
	
	@Override
	public void init(ServletConfig config)
    throws ServletException {
		super.init(config);
		logger.info("Loading OAuth Servlet");
		
		// load the context-parameters 
		ServletContext context = config.getServletContext();
		requestTokenURL = context.getInitParameter(PARAM_REQUEST_TOKEN_URL);
		if(requestTokenURL == null)
			requestTokenURL = "/requestToken";
		accessTokenURL = context.getInitParameter(PARAM_ACCESS_TOKEN_URL);
		if(accessTokenURL == null)
			accessTokenURL = "/accessToken";

		logger.info("Request token URL: "+ requestTokenURL);
		logger.info("Access token URL: "+ accessTokenURL);
		
		// now load the provider and validator
		provider = OAuthUtils.getOAuthProvider(context);
		validator = OAuthUtils.getValidator(context, provider);
		logger.debug("OAuthServlet loaded");
	}
	
	@Override
	protected void service(HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException,
	IOException{
		String pathInfo = req.getPathInfo();
		logger.debug("Serving "+pathInfo);
		logger.debug("Query "+req.getQueryString());
		if(pathInfo.equals(requestTokenURL))
			serveRequestToken(req, resp);
		else if(pathInfo.equals(accessTokenURL))
			serveAccessToken(req, resp);
		else
			resp.sendError(HttpURLConnection.HTTP_NOT_FOUND);
	}

	private void serveRequestToken(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		logger.debug("Request token");
		OAuthMessage message = OAuthUtils.readMessage(req);
		try{
			// require some parameters
			message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
					OAuth.OAUTH_SIGNATURE_METHOD,
					OAuth.OAUTH_SIGNATURE,
					OAuth.OAUTH_TIMESTAMP,
					OAuth.OAUTH_NONCE,
					OAuth.OAUTH_CALLBACK);
			logger.debug("Parameters present");

			String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
			// load the OAuth Consumer
			org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = provider.getConsumer(consumerKey);
			
			// create some structures for net.oauth
			OAuthConsumer _consumer = new OAuthConsumer(null, consumerKey, consumer.getSecret(), null);
			OAuthAccessor accessor = new OAuthAccessor(_consumer);
			
			// validate the message
			validator.validateMessage(message, accessor, null);

			// create a new Request Token
			OAuthToken token = provider.makeRequestToken(consumerKey, message.getParameter(OAuth.OAUTH_CALLBACK));

			// send the Token information to the Client
			OAuthUtils.sendValues(resp, OAuth.OAUTH_TOKEN, token.getToken(),OAuth.OAUTH_TOKEN_SECRET, token.getSecret(), OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM, "true");
			resp.setStatus(HttpURLConnection.HTTP_OK);
			logger.debug("All OK");

		} catch (OAuthException x) {
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), x.getHttpCode(), provider);
		} catch (OAuthProblemException x) {
			OAuthUtils.makeErrorResponse(resp, x.getProblem(), OAuthUtils.getHttpCode(x), provider);
		} catch (Exception x) {
			logger.error("Exception ", x);
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
		}
	}


	private void serveAccessToken(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		logger.debug("Access token");
		OAuthMessage message = OAuthUtils.readMessage(req);
		try{
			// request some parameters
			message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
					OAuth.OAUTH_TOKEN,
					OAuth.OAUTH_SIGNATURE_METHOD,
					OAuth.OAUTH_SIGNATURE,
					OAuth.OAUTH_TIMESTAMP,
					OAuth.OAUTH_NONCE,
					OAuthUtils.OAUTH_VERIFIER_PARAM);

			logger.debug("Parameters present");
			
			// load some parameters
			String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
			String requestTokenString = message.getParameter(OAuth.OAUTH_TOKEN);
			String verifier = message.getParameter("oauth_verifier");
			
			// get the Request Token to exchange
			OAuthToken requestToken = provider.getRequestToken(consumerKey, requestTokenString);
			
			// build some structures for net.oauth
			OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, requestToken.getConsumer().getSecret(), null);
			OAuthAccessor accessor = new OAuthAccessor(consumer);
			accessor.requestToken = requestTokenString;
			accessor.tokenSecret = requestToken.getSecret();

			// verify the message signature
			validator.validateMessage(message, accessor, requestToken);

			// exchange the Request Token
			OAuthToken tokens = provider.makeAccessToken(consumerKey, requestTokenString, verifier);

			// send the Access Token
			OAuthUtils.sendValues(resp, OAuth.OAUTH_TOKEN, tokens.getToken(),OAuth.OAUTH_TOKEN_SECRET, tokens.getSecret());
			resp.setStatus(HttpURLConnection.HTTP_OK);
			logger.debug("All OK");

		} catch (OAuthException x) {
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), x.getHttpCode(), provider);
		} catch (OAuthProblemException x) {
			OAuthUtils.makeErrorResponse(resp, x.getProblem(), OAuthUtils.getHttpCode(x), provider);
		} catch (Exception x) {
			logger.error("Exception ", x);
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
		}
	}
}
