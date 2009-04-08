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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class TwitterClient
{
   static final String friendTimeline = "http://twitter.com/statuses/friends_timeline.xml";

   public static void main(String[] args) throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      TwitterResource twitter = ProxyFactory.create(TwitterResource.class,
            "http://twitter.com", createClient(args[0], args[1]));
      System.out.println("===> first run");
      printStatuses(twitter.getFriendsTimelines());
      
      twitter
            .updateStatus("I programmatically tweeted with the RESTEasy Client at "
                  + new Date());
      
      System.out.println("===> second run");
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

   private static HttpClient createClient(String userId, String password)
   {
      Credentials credentials = new UsernamePasswordCredentials(userId,
            password);
      HttpClient httpClient = new HttpClient();
      httpClient.getState().setCredentials(AuthScope.ANY, credentials);
      httpClient.getParams().setAuthenticationPreemptive(true);
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
