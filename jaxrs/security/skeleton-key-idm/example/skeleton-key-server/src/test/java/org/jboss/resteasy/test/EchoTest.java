package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.skeleton.key.keystone.model.User;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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
}

