package org.jboss.resteasy.examples.twitter;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TwitterClient {
	private final static DateTimeFormatter TWITTER_DATE_FORMATTER = DateTimeFormat
			.forPattern("EEE MMM dd HH:mm:ss Z yyyy");

	public static class DateTimeAdapter extends XmlAdapter<String, DateTime> {

		@Override
		public String marshal(DateTime date) throws Exception {
			return TWITTER_DATE_FORMATTER.print(date);
		}

		@Override
		public DateTime unmarshal(String string) throws Exception {
			try {
				return TWITTER_DATE_FORMATTER.parseDateTime(string);
			} catch (IllegalArgumentException e) {
				System.err.println(String.format(
						"Could not parse date string '%s'", string));
				return null;
			}
		}
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
		@XmlJavaTypeAdapter(value = DateTimeAdapter.class)
		public DateTime created;

		public String toString() {
			return String.format("== %s: %s (%s)", user.name, text, created
					.toDate().toString());

		}
	}

	public static class User {
		public String name;
	}

	public static void main(String[] args) throws Exception {
		final String friendsTimeline = "http://twitter.com/statuses/friends_timeline.xml";
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		Credentials credentials = new UsernamePasswordCredentials(args[0],
				args[1]);
		HttpClient httpClient = new HttpClient();
		httpClient.getState().setCredentials(AuthScope.ANY, credentials);
		httpClient.getParams().setAuthenticationPreemptive(true);
		displayStatuses(new ClientRequest(friendsTimeline, httpClient).get(
				Statuses.class).getEntity());
		
		// uncomment to perform a 'write' operation 
//		String status = "I programmatically tweeted with the RESTEasy Client at "
//				+ new Date();
//		display(new ClientRequest(
//				"http://twitter.com/statuses/update.xml", httpClient)
//				.formParameter("status", status).post(Status.class).getEntity());
		 
	}

	private static void displayStatuses(Statuses statuses) {
		for (Status status : statuses.status) {
			display(status);
		}
	}

	private static void display(Status status) {
		System.out.println(status);
	}
}
