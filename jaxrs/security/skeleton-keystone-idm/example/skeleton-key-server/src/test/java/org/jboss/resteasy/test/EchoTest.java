package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.skeleton.key.keystone.model.Mappers;
import org.jboss.resteasy.skeleton.key.keystone.model.User;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;

/**
 */
public class EchoTest
{
   @Test
   public void testUser() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      WebTarget skeletonKeyServer = client.target("http://localhost:8080/skeleton-key-server");
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("admin").password("geheim").idp(skeletonKeyServer).admin();
      User user = admin.users().get("1");
      System.out.println(user);
   }

   @Test
   public void testUserSigned() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      WebTarget skeletonKeyServer = client.target("http://localhost:8080/skeleton-key-server");
      ResteasyWebTarget adminServerTarget = client.target("http://localhost:8080/skeleton-key-server");
      String token = new SkeletonKeyClientBuilder().username("admin").password("geheim").idp(skeletonKeyServer).signed("Skeleton Key", adminServerTarget);
      System.out.println("token: " + token);
      Mappers.registerContextResolver(adminServerTarget.configuration());
      SkeletonKeyAdminClient admin = adminServerTarget.proxy(SkeletonKeyAdminClient.class);
      User user = admin.users().get("1");
      System.out.println(user);
   }

}

