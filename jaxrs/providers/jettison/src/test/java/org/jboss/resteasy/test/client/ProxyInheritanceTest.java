package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * RESTEASY-213
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyInheritanceTest extends BaseResourceTest
{
   @XmlAccessorType(XmlAccessType.NONE)
   @XmlRootElement(name = "user")
   public static class UserEntity
   {

      @XmlElement
      private String username;

      public UserEntity()
      {
         super();
      }

      public String getUsername()
      {
         return username;
      }

      public void setUsername(String username)
      {
         this.username = username;
      }

   }

   @Path("/user")
   @Produces({"application/xml", "application/json"})
   @Consumes({"application/xml", "application/json"})
   public static interface UserEntityWebservice extends CRUDEntityWebservice
   {

   }

   public static interface CRUDEntityWebservice
   {

      @POST
      @Path("/")
      public UserEntity create(UserEntity entity);
   }

   public static class MyService implements UserEntityWebservice
   {
      public UserEntity create(UserEntity entity)
      {
         return entity;
      }
   }


   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(MyService.class);
   }

   @Test
   public void testBasic()
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

      UserEntity u = new UserEntity();
      u.setUsername("user");

      UserEntityWebservice serviceClient = ProxyFactory.create(UserEntityWebservice.class, generateBaseUrl());
      UserEntity newUser = serviceClient.create(u);

      System.out.println("**** " + newUser.getUsername());
   }


}
