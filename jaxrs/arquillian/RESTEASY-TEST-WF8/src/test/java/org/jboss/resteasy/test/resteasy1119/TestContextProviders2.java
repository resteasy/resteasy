package org.jboss.resteasy.test.resteasy1119;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy1119.Customer;
import org.jboss.resteasy.resteasy1119.CustomerForm;
import org.jboss.resteasy.resteasy1119.Name;
import org.jboss.resteasy.resteasy1119.TestApplication;
import org.jboss.resteasy.resteasy1119.TestResource;
import org.jboss.resteasy.resteasy1119.Xop;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@SuppressWarnings("deprecation")
@RunWith(Arquillian.class)
public class TestContextProviders2 extends TestContextProviders
{ 
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1119.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(Customer.class, CustomerForm.class, Name.class, Xop.class)
            .addClass(TestContextProviders.class)
            .addAsWebInfResource("1119/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }

   @Override
   <T> T get(String path, Class<T> clazz, Annotation[] annotations) throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1119" + path);
      ClientResponse<T> response = request.get(clazz);
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      T entity = response.getEntity(clazz, null, annotations);
      return entity;
   }

   @Override
   <S, T> T post(String path, S payload, MediaType mediaType,
         Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1119" + path);
      request.body(mediaType, payload, payload.getClass(), null, annotations);
      ClientResponse response = request.post();
      T entity = null;
      if (genericReturnType != null)
      {
         entity = (T) response.getEntity(returnType, genericReturnType);
      }
      else
      {
         entity = (T) response.getEntity(returnType);
      }
    
      return entity;
   }

}
