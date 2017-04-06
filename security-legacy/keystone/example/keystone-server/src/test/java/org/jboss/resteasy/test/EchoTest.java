package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.keystone.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.keystone.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.keystone.model.Mappers;
import org.jboss.resteasy.keystone.model.User;
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
      WebTarget keystoneServer = client.target("http://localhost:8080/keystone-server");
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("admin").password("geheim").idp(keystoneServer).admin();
      User user = admin.users().get("1");
      System.out.println(user);
   }

   @Test
   public void testUserSigned() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      WebTarget keystoneServer = client.target("http://localhost:8080/keystone-server");
      ResteasyWebTarget adminServerTarget = client.target("http://localhost:8080/keystone-server");
      String token = new SkeletonKeyClientBuilder().username("admin").password("geheim").idp(keystoneServer).signed("Keystone", adminServerTarget);
      System.out.println("token: " + token);
      Mappers.registerContextResolver(adminServerTarget.configuration());
      SkeletonKeyAdminClient admin = adminServerTarget.proxy(SkeletonKeyAdminClient.class);
      User user = admin.users().get("1");
      System.out.println(user);
   }

}

