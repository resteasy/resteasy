package org.jboss.resteasy.examples.twitter;

import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class TwitterClient
{
   static final String friendTimeline = "http://twitter.com/statuses/friends_timeline.xml";

   public static void main(String[] args) throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      final ClientExecutor clientExecutor = new ApacheHttpClient4Executor(createClient(args[0], args[1]));
      TwitterResource twitter = ProxyFactory.create(TwitterResource.class,
            "http://twitter.com", clientExecutor);
      
      System.out.println("===> first run");
      twitter
      .updateStatus("1st: I programmatically tweeted with the RESTEasy Client at "
            + new Date());
      printStatuses(twitter.getFriendsTimelines());
      
      System.out.println("===> second run");
      twitter
      .updateStatus("2nd: I programmatically tweeted with the RESTEasy Client at "
            + new Date());
      printStatuses(twitter.getFriendsTimelines());
   }

   public static interface TwitterResource
   {
      @Path("/statuses/friends_timeline.xml")
      @GET
      Statuses getFriendsTimelines();

      @Path("/statuses/update.xml")
      @POST
      Status updateStatus(@FormParam("status") String status);
   }

   private static void printStatuses(Statuses statuses)
   {
      for (Status status : statuses.status)
         System.out.println(status);
   }

	private static HttpClient createClient(String userId, String password) {

		Credentials credentials = new UsernamePasswordCredentials(userId,
				password);
		HttpClient httpClient = new DefaultHttpClient();
		((DefaultHttpClient) httpClient).getCredentialsProvider()
				.setCredentials(AuthScope.ANY, credentials);

		return httpClient;
	}

   @XmlRootElement
   public static class Statuses
   {
      public List<Status> status;
   }

   @XmlRootElement
   public static class Status
   {
      public String text;
      public User user;

      @XmlElement(name = "created_at")
      @XmlJavaTypeAdapter(value = DateAdapter.class)
      public Date created;

      public String toString()
      {
         return String.format("== %s: %s (%s)", user.name, text, created);
      }
   }

   public static class User
   {
      public String name;
   }

}
