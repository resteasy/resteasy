package org.jboss.resteasy.auth.oauth;

import java.util.Set;

import org.jboss.resteasy.auth.oauth.i18n.Messages;

/**
 * Used to make sure the OAuthProvider implementer does not return null values.
 * If any null is returned, throws a RuntimeException
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class OAuthProviderChecker implements OAuthProvider {
	
	private OAuthProvider provider;

	public OAuthProviderChecker(OAuthProvider provider) {
		this.provider = provider;
	}

	private <T> T checkNull(T arg) {
		if(arg == null)
		   throw new RuntimeException(Messages.MESSAGES.oAuthProviderShouldNotReturnNull());
		return arg;
	}
	
	public OAuthConsumer registerConsumer(String consumerKey, String displayName, String connectURI) 
	    throws OAuthException {
        return checkNull(provider.registerConsumer(consumerKey, displayName, connectURI));
    }

	public OAuthConsumer getConsumer(String consumerKey) throws OAuthException {
		return checkNull(provider.getConsumer(consumerKey));
	}

	public String getRealm() {
		return checkNull(provider.getRealm());
	}

	public OAuthRequestToken getRequestToken(String consumerKey, String requestKey)
			throws OAuthException {
		return checkNull(provider.getRequestToken(consumerKey, requestKey));
	}

	public OAuthToken getAccessToken(String consumerKey, String accessKey)
	throws OAuthException {
		return checkNull(provider.getAccessToken(consumerKey, accessKey));
	}

	public void checkTimestamp(OAuthToken token, long timestamp) throws OAuthException {
		provider.checkTimestamp(token, timestamp);
	}

	public OAuthToken makeAccessToken(String consumerKey,
			String requestKey, String verifier) throws OAuthException {
		return checkNull(provider.makeAccessToken(consumerKey, requestKey, verifier));
	}

	public OAuthToken makeRequestToken(String consumerKey, String callback, 
	                                   String[] scopes, String[] permissions)
			throws OAuthException {
		return checkNull(provider.makeRequestToken(consumerKey, callback, scopes, permissions));
	}

	public String authoriseRequestToken(String consumerKey, String requestKey)
			throws OAuthException {
		return checkNull(provider.authoriseRequestToken(consumerKey, requestKey));
	}

    public void registerConsumerScopes(String consumerKey, String[] scopes)
            throws OAuthException {
        provider.registerConsumerScopes(consumerKey, scopes);
    }
    
    public void registerConsumerPermissions(String consumerKey, String[] permissions)
        throws OAuthException {
        provider.registerConsumerPermissions(consumerKey, permissions);
    }

    public Set<String> convertPermissionsToRoles(String[] permissions) {
        return provider.convertPermissionsToRoles(permissions);
    }
}
