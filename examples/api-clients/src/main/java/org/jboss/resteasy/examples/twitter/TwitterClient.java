package org.jboss.resteasy.examples.twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.oauth.OAuthMessage;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class TwitterClient {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("TwitterClient [help | <operation>]");
			return;
		} else if ("help".equals(args[0])) {
			System.err.println("\n*Please see the readme.txt*\n");
			return;
		}

		new TwitterClient().execute(args[0]);
	}

	private Properties props;
	private File propFile;
	private TwitterClientOAuthHelper oauthHelper;

	private TwitterResource twitter;

	public TwitterClient() {
		try {
			props = new Properties();
			propFile = new File(TwitterClient.class.getResource(
					"/twitter.properties").getFile());
			props.load(new FileInputStream(propFile));
			oauthHelper = new TwitterClientOAuthHelper(props, propFile);

			RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
			final ClientExecutor clientExecutor = new ApacheHttpClient4Executor(
					createClient());
			twitter = ProxyFactory.create(TwitterResource.class,
					"http://api.twitter.com/1", clientExecutor);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void execute(String operation) throws Exception {
		if ("request".equals(operation)) {
			oauthHelper.request();
			System.out.println(propFile.getCanonicalPath() + " updated");
		} else if ("authorize".equals(operation)) {
			String url = oauthHelper.authorize();
			System.out.println("Paste this in a browser:");
			System.out.println(url);
		} else if ("access".equals(operation)) {
			oauthHelper.access();
			System.out.println(propFile.getCanonicalPath() + " updated");
		} else if ("query".equals(operation)) {
			printStatuses(twitter
					.getFriendsTimelines(oauthHelper
							.prepareOAuthHeaderForAccesingResources(
									props,
									OAuthMessage.GET,
									"http://api.twitter.com/1/statuses/friends_timeline.xml",
									null)));
		} else if ("update".equals(operation)) {
			String status = "I programmatically tweeted with the RESTEasy Client at "
					+ new Date();
			twitter.updateStatus(oauthHelper
					.prepareOAuthHeaderForAccesingResources(props,
							OAuthMessage.POST,
							"http://api.twitter.com/1/statuses/update.xml",
							status), status);
		}

	}

	public static interface TwitterResource {
		@Path("/statuses/friends_timeline.xml")
		@GET
		Statuses getFriendsTimelines(@HeaderParam("Authorization") String header);

		@Path("/statuses/update.xml")
		@POST
		Status updateStatus(@HeaderParam("Authorization") String header,
				@FormParam("status") String status);
	}

	private static void printStatuses(Statuses statuses) {
		for (Status status : statuses.status)
			System.out.println(status);
	}

	private static HttpClient createClient() {
		HttpClient httpClient = new DefaultHttpClient();
				
		// Use Proxy to access if you are in one of the countries that cannot connects to twitter directly.   
//		HttpHost proxy = new HttpHost("your_proxy_url", your_proxy_port);
//		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
//				proxy);
				
		return httpClient;
	}

	@XmlRootElement
	public static class Statuses {
		public List<Status> status;
	}

	@XmlRootElement
	public static class Status {
		public String text;
		public User user;

		@XmlElement(name = "created_at")
		@XmlJavaTypeAdapter(value = DateAdapter.class)
		public Date created;

		public String toString() {
			return String.format("== %s: %s (%s)", user.name, text, created);
		}
	}

	public static class User {
		public String name;
	}

}
