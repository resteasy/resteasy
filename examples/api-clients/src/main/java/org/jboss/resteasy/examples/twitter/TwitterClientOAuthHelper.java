package org.jboss.resteasy.examples.twitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;
import net.oauth.client.httpclient4.HttpClientPool;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

public class TwitterClientOAuthHelper {

	private Properties props;
	private File propFile;

	public TwitterClientOAuthHelper(Properties props, File propFile) {
		super();
		this.props = props;
		this.propFile = propFile;
	}

	private void updateProperties(String msg) throws IOException {
		props.store(new FileOutputStream(propFile), msg);
	}

	private OAuthAccessor createOAuthAccessor() {
		String consumerKey = props.getProperty("consumerKey");
		String callbackUrl = null;
		String consumerSecret = props.getProperty("consumerSecret");
		String reqUrl = props.getProperty("requestUrl");
		String authzUrl = props.getProperty("authorizationUrl");
		String accessUrl = props.getProperty("accessUrl");
		OAuthServiceProvider provider = new OAuthServiceProvider(reqUrl,
				authzUrl, accessUrl);
		OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey,
				consumerSecret, provider);
		return new OAuthAccessor(consumer);
	}

	private OAuthMessage sendRequest(Map map, String url) throws IOException,
			URISyntaxException, OAuthException {
		List<Map.Entry> params = new ArrayList<Map.Entry>();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry p = (Map.Entry) it.next();
			params.add(new OAuth.Parameter((String) p.getKey(), (String) p
					.getValue()));
		}
		OAuthAccessor accessor = createOAuthAccessor();
		accessor.tokenSecret = props.getProperty("tokenSecret");
		OAuthClient client = new OAuthClient(
				new HttpClient4(new SingleClient()));
		return client.invoke(accessor, "GET", url, params);
	}

	public String prepareOAuthHeaderForAccesingResources(Properties props,
			String method, String resource, String updateStatus)
			throws Exception {
		OAuthMessage message = new OAuthMessage(method, resource, null, null);
		message.addParameter(OAuth.OAUTH_NONCE, System.nanoTime() + "");
		message.addParameter(OAuth.OAUTH_VERSION, OAuth.VERSION_1_0);
		message.addParameter(OAuth.OAUTH_TIMESTAMP,
				(System.currentTimeMillis() / 1000) + "");
		message.addParameter(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
		message.addParameter(OAuth.OAUTH_TOKEN, props
				.getProperty("accessToken"));
		message.addParameter(OAuth.OAUTH_CONSUMER_KEY, props
				.getProperty("consumerKey"));

		if (updateStatus != null) {
			// the parameter used by twitter on
			message.addParameter("status", updateStatus);
		}

		OAuthServiceProvider provider = new OAuthServiceProvider(props
				.getProperty("requestUrl"), props
				.getProperty("authorizationUrl"), props
				.getProperty("accessUrl"));
		OAuthConsumer consumer = new OAuthConsumer("", props
				.getProperty("consumerKey"), props
				.getProperty("consumerSecret"), provider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);
		accessor.tokenSecret = props.getProperty("tokenSecret");
		message.sign(accessor);

		return message.getAuthorizationHeader(resource);
	}

	public void request() throws Exception {
		OAuthAccessor accessor = createOAuthAccessor();
		HttpClient4 httpClient = new HttpClient4(new SingleClient());
		OAuthClient client = new OAuthClient(httpClient);
		client.getRequestToken(accessor);
		props.setProperty("requestToken", accessor.requestToken);
		props.setProperty("tokenSecret", accessor.tokenSecret);
		updateProperties("Last action: added requestToken");
	}

	public String authorize() throws Exception {
		// just print the redirect
		Properties paramProps = new Properties();
		paramProps
				.setProperty("application_name", props.getProperty("appName"));
		paramProps
				.setProperty("oauth_token", props.getProperty("requestToken"));
		OAuthAccessor accessor = createOAuthAccessor();
		OAuthMessage response = sendRequest(paramProps,
				accessor.consumer.serviceProvider.userAuthorizationURL);
		return response.URL;
	}

	public void access() throws Exception {
		Properties paramProps = new Properties();
		paramProps
				.setProperty("oauth_token", props.getProperty("requestToken"));
		OAuthMessage response = sendRequest(paramProps, props
				.getProperty("accessUrl"));

		props.setProperty("accessToken", response.getParameter("oauth_token"));
		props.setProperty("tokenSecret", response
				.getParameter("oauth_token_secret"));
		props.setProperty("userId", response.getParameter("user_id"));
		updateProperties("Last action: added accessToken");
	}

	public class SingleClient implements HttpClientPool {
		SingleClient() {
			org.apache.http.client.HttpClient client = new DefaultHttpClient();
			ClientConnectionManager mgr = client.getConnectionManager();
			if (!(mgr instanceof ThreadSafeClientConnManager)) {
				HttpParams params = client.getParams();
				client = new DefaultHttpClient(new ThreadSafeClientConnManager(
						params, mgr.getSchemeRegistry()), params);
			}

			// Use Proxy to access if you are in one of the countries that cannot connects to twitter directly.   
			// HttpHost proxy = new HttpHost("your_proxy_url", your_proxy_port);
			// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			// proxy);
			
			this.client = client;
		}

		private final org.apache.http.client.HttpClient client;

		public org.apache.http.client.HttpClient getHttpClient(URL server) {
			return client;
		}
	}

}
