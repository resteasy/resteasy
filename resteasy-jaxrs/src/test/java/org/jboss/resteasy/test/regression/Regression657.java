package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Regression657 extends BaseResourceTest
{
   public static class OhaUserModel
   {
      private String username;

      public OhaUserModel(String username)
      {
         this.username = username;
      }

      @Override
      public String toString()
      {
         return username;
      }
   }

   public interface BaseCrudService<T>
   {

   }

   @Path("/platform")
   public interface PlatformServiceLocator
   {

      @Path("/users/{user}")
      public UserRestService getUserService(
              @HeaderParam("entity")
              String entity,
              @HeaderParam("ticket")
              String ticket,
              @PathParam("user")
              String userId
      );
   }

   @Path("/users")
   public interface UserRestService extends BaseUserService
   {
   }

   public interface BaseUserService extends BaseCrudService<OhaUserModel>
   {


      @GET
      @Produces("text/plain")
      @Path("data/ada/{user}")
      public OhaUserModel getUserDataByAdaId(
              @PathParam("user")
              String adaId);
   }


   public static class PlatformServiceLocatorImpl implements PlatformServiceLocator
   {
      @Override
      public UserRestService getUserService(String entity, String ticket, String userId)
      {
         return new UserRestService()
         {
            @Override
            public OhaUserModel getUserDataByAdaId(String adaId)
            {
               return new OhaUserModel("bill");
            }
         };
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(PlatformServiceLocatorImpl.class);
   }

   @Test
   public void test657() throws Exception
   {
      ClientRequest request = new ClientRequest(generateBaseUrl() + "/platform/users/89080/data/ada/jsanchez110");
      String s = request.getTarget(String.class);
      Assert.assertEquals(s, "bill");

   }

}
