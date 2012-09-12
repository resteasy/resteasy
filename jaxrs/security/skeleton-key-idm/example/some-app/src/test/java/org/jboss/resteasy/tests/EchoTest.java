package org.jboss.resteasy.tests;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyClientBuilder;
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
      WebTarget appTarget = client.target("http://localhost:8080/skeleton-app");
      new SkeletonKeyClientBuilder().username("someuser").password("geheim").idp(skeletonKeyServer).authenticate("Skeleton App", appTarget);

      String message = appTarget.path("user/users.txt").request().get(String.class);
      Assert.assertEquals("Hello User", message);

      Response response = appTarget.path("admin/admins.txt").request().get();
      Assert.assertEquals(403, response.getStatus());

   }
}

