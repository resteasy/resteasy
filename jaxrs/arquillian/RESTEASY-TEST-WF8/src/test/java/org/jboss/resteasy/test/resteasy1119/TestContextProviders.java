package org.jboss.resteasy.test.resteasy1119;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.AnnotationLiteral;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.jboss.resteasy.resteasy1119.Customer;
import org.jboss.resteasy.resteasy1119.CustomerForm;
import org.jboss.resteasy.resteasy1119.Name;
import org.jboss.resteasy.resteasy1119.TestApplication;
import org.jboss.resteasy.resteasy1119.TestResource;
import org.jboss.resteasy.resteasy1119.Xop;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * RESTEASY-1119
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2015
 */
@SuppressWarnings("deprecation")
@RunWith(Arquillian.class)
public class TestContextProviders
{
   static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
   static final MediaType MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
   static final MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
   
   static final javax.ws.rs.core.GenericType<List<Name>> LIST_NAME_TYPE = new javax.ws.rs.core.GenericType<List<Name>>() {};
   
   public abstract static class S1 extends AnnotationLiteral<PartType> implements PartType
   {
      private static final long serialVersionUID = 1L;
   }
   public static final Annotation PART_TYPE_APPLICATION_XML = new S1() 
   {
      private static final long serialVersionUID = 1L;
      @Override public String value() {return "application/xml";}
   };
   
   public abstract static class S2 extends AnnotationLiteral<MultipartForm> implements MultipartForm
   {
      private static final long serialVersionUID = 1L;
   }
   public static final Annotation MULTIPART_FORM = new S2() 
   {
      private static final long serialVersionUID = 1L;
   };
   
   public abstract static class S3 extends AnnotationLiteral<XopWithMultipartRelated> implements XopWithMultipartRelated
   {
      private static final long serialVersionUID = 1L;
   }
   public static final Annotation XOP_WITH_MULTIPART_RELATED = new S3() 
   {
      private static final long serialVersionUID = 1L;
   };
   
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   protected enum Version {TWO, THREE};
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1119.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(Customer.class, CustomerForm.class, Name.class, Xop.class)
            .addAsWebInfResource("1119/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testGetFormData() throws Exception
   {
      doTestGetFormData(Version.TWO);
      doTestGetFormData(Version.THREE);
   }
   
   public void doTestGetFormData(Version version) throws Exception
   {
      MultipartFormDataInput entity = get(version, "/get/form", MultipartFormDataInput.class);
      
      // Get parts by name.
      Customer c = entity.getFormDataPart("bill", Customer.class, null);
      System.out.println("part 1: " + c.getName());
      Assert.assertEquals("Bill", c.getName());
      String s = entity.getFormDataPart("bob", String.class, null);
      System.out.println("part 2: " + s);
      Assert.assertEquals("Bob", s);
      
      // Iterate over list of parts.
      Map<String, List<InputPart>> map = entity.getFormDataMap();
      for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();
         System.out.println("key: " + key);
         List<InputPart> list = map.get(key);
         for (Iterator<InputPart> it2 = list.iterator(); it2.hasNext(); )
         {
            InputPart inputPart = it2.next();
            System.out.println("media type: " + inputPart.getMediaType());
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType()))
            {
               c = inputPart.getBody(Customer.class, null);
               Assert.assertEquals("Bill", c.getName());             
            }
            else
            {
               s = inputPart.getBody(String.class, null);
               Assert.assertEquals("Bob", s);
            }
         }
      }
   }
   
   @Test
   public void testGetMixed() throws Exception
   {
      doTestGetMixed(Version.TWO);
      doTestGetMixed(Version.THREE);
   }
   
   void doTestGetMixed(Version version) throws Exception
   {
      MultipartInput entity = get(version, "/get/mixed", MultipartInput.class);
      
      // Iterate over list of parts.
      List<InputPart> parts = entity.getParts();
      for (Iterator<InputPart> it = parts.iterator(); it.hasNext(); )
      {
         InputPart inputPart = it.next();
         System.out.println("media type: " + inputPart.getMediaType());
         if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType()))
         {
            Customer c = inputPart.getBody(Customer.class, null);
            Assert.assertEquals("Bill", c.getName());             
         }
         else
         {
            String s = inputPart.getBody(String.class, null);
            Assert.assertEquals("Bob", s);
         }   
      }
   }

   @Test
   public void testGetList() throws Exception
   {
      doTestGetList(Version.TWO);
      doTestGetList(Version.THREE);
   }
   
   void doTestGetList(Version version) throws Exception
   {
      MultipartInput entity = get(version, "/get/list", MultipartInput.class);
      
      // Iterate over list of parts.
      List<InputPart> parts = entity.getParts();
      Set<String> customers = new HashSet<String>();
      for (Iterator<InputPart> it = parts.iterator(); it.hasNext(); )
      {
         InputPart inputPart = it.next();
         customers.add(inputPart.getBody(Customer.class, null).getName());
      }
      Assert.assertEquals(2, customers.size());
      Assert.assertTrue(customers.contains("Bill"));
      Assert.assertTrue(customers.contains("Bob"));
   }

   @Test
   public void testGetMap() throws Exception
   {
      doTestGetMap(Version.TWO);
      doTestGetMap(Version.THREE);
   }
   
   public void doTestGetMap(Version version) throws Exception
   {
      MultipartFormDataInput entity = get(version, "/get/map", MultipartFormDataInput.class);
      
      // Get parts by name.
      Customer c = entity.getFormDataPart("bill", Customer.class, null);
      System.out.println("bill: " + c.getName());
      Assert.assertEquals("Bill", c.getName());
      c = entity.getFormDataPart("bob", Customer.class, null);
      System.out.println("bob: " + c.getName());
      Assert.assertEquals("Bob", c.getName());

      // Iterate over map of parts.
      Map<String, List<InputPart>> map = entity.getFormDataMap();
      Set<String> customers = new HashSet<String>();
      for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();
         List<InputPart> list = map.get(key);
         for (Iterator<InputPart> it2 = list.iterator(); it2.hasNext(); )
         {
            InputPart inputPart = it2.next();
            customers.add(inputPart.getBody(Customer.class, null).getName());
         }
      }
      Assert.assertEquals(2, customers.size());
      Assert.assertTrue(customers.contains("Bill"));
      Assert.assertTrue(customers.contains("Bob"));
   }

   @Test
   public void testGetRelated() throws Exception
   {
      doTestGetRelated(Version.TWO);
      doTestGetRelated(Version.THREE);
   }
   
   void doTestGetRelated(Version version) throws Exception
   {
      MultipartRelatedInput entity = get(version, "/get/related", MultipartRelatedInput.class);
      
      // Iterate over map of parts.
      Map<String, InputPart> map = entity.getRelatedMap();
      Set<String> keys = map.keySet();
      Assert.assertEquals(2, keys.size());
      Assert.assertTrue(keys.contains("bill"));
      Assert.assertTrue(keys.contains("bob"));
      Set<String> parts = new HashSet<String>();
      for (Iterator<InputPart> it = map.values().iterator(); it.hasNext(); )
      {
         parts.add(it.next().getBody(String.class, null));
      }
      Assert.assertTrue(parts.contains("Bill"));
      Assert.assertTrue(parts.contains("Bob"));
   }
   
   @Test
   public void testGetMultipartForm() throws Exception
   {
      doTestGetMultipartForm(Version.TWO);
      doTestGetMultipartForm(Version.THREE);
   }
   
   void doTestGetMultipartForm(Version version) throws Exception
   {
      Annotation[] annotations = new Annotation[1];
      annotations[0] = MULTIPART_FORM;
      CustomerForm form = get(version, "/get/multipartform", CustomerForm.class, annotations);
      Customer customer = form.getCustomer();
      Assert.assertEquals("Bill", customer.getName());
   }
   
   @Test
   public void testGetXop() throws Exception
   {
      doTestGetXop(Version.TWO);
      doTestGetXop(Version.THREE);
   }
   
   void doTestGetXop(Version version) throws Exception
   {
      Annotation[] annotations = new Annotation[1];
      annotations[0] = XOP_WITH_MULTIPART_RELATED;
      Xop xop = get(version, "/get/xop", Xop.class, annotations);
      System.out.println("xop: " + new String(xop.getBytes()));
      Assert.assertEquals("goodbye world", new String(xop.getBytes()));
   }
   
   @Test
   public void testPostMixed() throws Exception
   {
      doTestPostMixed(Version.TWO);
      doTestPostMixed(Version.THREE);
   }
   
   @SuppressWarnings("unchecked")
   void doTestPostMixed(Version version) throws Exception
   {
      MultipartOutput output = new MultipartOutput();
      output.addPart(new Customer("Bill"), MediaType.APPLICATION_XML_TYPE);
      output.addPart("Bob", MediaType.TEXT_PLAIN_TYPE);
      Annotation[] annotations = new Annotation[1];
      annotations[0] = PART_TYPE_APPLICATION_XML;
      List<Name> names = new ArrayList<Name>();
      names = post(version, "/post/mixed", output, MULTIPART_MIXED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
      Assert.assertEquals(2, names.size());
      Assert.assertTrue(names.contains(new Name("Bill")));
      Assert.assertTrue(names.contains(new Name("Bob"))); 
   }

   @Test
   public void testPostFormData() throws Exception
   {
      doTestPostFormData(Version.TWO);
      doTestPostFormData(Version.THREE);
   }

   @SuppressWarnings("unchecked")
   public void doTestPostFormData(Version version) throws Exception
   {

      MultipartFormDataOutput output = new MultipartFormDataOutput();
      output.addFormData("bill", new Customer("Bill"), MediaType.APPLICATION_XML_TYPE);
      output.addFormData("bob", "Bob", MediaType.TEXT_PLAIN_TYPE);
      Annotation[] annotations = new Annotation[1];
      annotations[0] = PART_TYPE_APPLICATION_XML;
      List<Name> names = new ArrayList<Name>();
      names = post(version, "/post/form", output, MULTIPART_FORM_DATA, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
      Assert.assertEquals(2, names.size());
      Assert.assertTrue(names.contains(new Name("Bill")));
      Assert.assertTrue(names.contains(new Name("Bob"))); 
   }

   @Test
   public void testPostList() throws Exception
   {
      doTestPostList(Version.TWO);
      doTestPostList(Version.THREE);
   }
   
   @SuppressWarnings("unchecked")
   public void doTestPostList(Version version) throws Exception
   {
      List<Customer> customers = new ArrayList<Customer>();
      customers.add(new Customer("Bill"));
      customers.add(new Customer("Bob"));
      Annotation[] annotations = new Annotation[1];
      annotations[0] = PART_TYPE_APPLICATION_XML;
      List<Name> names = new ArrayList<Name>();
      names = post(version, "/post/list", customers, MULTIPART_MIXED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
      Assert.assertEquals(2, names.size());
      Assert.assertTrue(names.contains(new Name("Bill")));
      Assert.assertTrue(names.contains(new Name("Bob"))); 
   }
   
   @Test
   public void testPostMap() throws Exception
   {
      doTestPostMap(Version.TWO);
      doTestPostMap(Version.THREE);
   }
   
   @SuppressWarnings("unchecked")
   public void doTestPostMap(Version version) throws Exception
   {
      Map<String, Customer> customers = new HashMap<String, Customer>();
      customers.put("bill", new Customer("Bill"));
      customers.put("bob", new Customer("Bob"));
      Annotation[] annotations = new Annotation[1];
      annotations[0] = PART_TYPE_APPLICATION_XML;
      List<Name> names = new ArrayList<Name>();
      names = post(version, "/post/map", customers, MULTIPART_FORM_DATA, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
      Assert.assertEquals(2, names.size());
      Assert.assertTrue(names.contains(new Name("bill:Bill")));
      Assert.assertTrue(names.contains(new Name("bob:Bob"))); 
   }
   
   @Test
   public void testPostRelated() throws Exception
   {
      doTestPostRelated(Version.TWO);
      doTestPostRelated(Version.THREE);
   }
   
   @SuppressWarnings("unchecked")
   void doTestPostRelated(Version version) throws Exception
   {
      MultipartRelatedOutput output = new MultipartRelatedOutput();
      output.setStartInfo("text/html");
      output.addPart("Bill", new MediaType("image", "png"), "bill", "binary");
      output.addPart("Bob", new MediaType("image", "png"), "bob", "binary");
      Annotation[] annotations = new Annotation[1];
      annotations[0] = PART_TYPE_APPLICATION_XML;
      List<Name> names = new ArrayList<Name>();
      names = post(version, "/post/related", output, MULTIPART_RELATED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
      Assert.assertEquals(2, names.size());
      Assert.assertTrue(names.contains(new Name("Bill")));
      Assert.assertTrue(names.contains(new Name("Bob")));
   }
   
   @Test
   public void testPostMultipartForm() throws Exception
   {
      doTestPostMultipartForm(Version.TWO);
      doTestPostMultipartForm(Version.THREE);
   }
   
   void doTestPostMultipartForm(Version version) throws Exception
   {
      CustomerForm form = new CustomerForm();
      form.setCustomer(new Customer("Bill"));
      Annotation[] annotations = new Annotation[1];
      annotations[0] = MULTIPART_FORM;
      String name = post(version, "/post/multipartform", form, MULTIPART_FORM_DATA, String.class, null, annotations);
      System.out.println("name: " + name);
      Assert.assertEquals("Bill", name);
   }
   
   @Test
   public void testPostXop() throws Exception
   {
      doTestPostXop(Version.TWO);
      doTestPostXop(Version.THREE);
   }

   void doTestPostXop(Version version) throws Exception
   {
      Xop xop = new Xop("hello world".getBytes());
      Annotation[] annotations = new Annotation[1];
      annotations[0] = XOP_WITH_MULTIPART_RELATED;
      String s = post(version, "/post/xop", xop, MULTIPART_RELATED, String.class, null, annotations);
      Assert.assertEquals("hello world", s);
   }

   <T> T get(Version version, String path, Class<T> clazz) throws Exception
   {
      return get(version, path, clazz, null);
   }
   
   <T> T get(Version version, String path, Class<T> clazz, Annotation[] annotations) throws Exception
   {
      switch (version)
      {
         case TWO:
         {
            ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1119" + path);
            ClientResponse<T> response = request.get(clazz);
            System.out.println("status: " + response.getStatus());
            Assert.assertEquals(200, response.getStatus());
            T entity = response.getEntity(clazz, null, annotations);
            return entity;
         }
            
         case THREE:
         {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:8080/RESTEASY-1119" + path);
            Response response = target.request().get();
            System.out.println("status: " + response.getStatus());
            Assert.assertEquals(200, response.getStatus());
            T entity = response.readEntity(clazz, annotations);
            return entity;
         }
         
         default:
            throw new Exception("Unknown version: " + version);
      }
   }
   
//   @SuppressWarnings({"unchecked"})
//   <S, T> T post(Version version, String path, S payload, MediaType mediaType, Class<T> returnType, javax.ws.rs.core.GenericType<T> genericReturnType, Annotation[] annotations) throws Exception
//   {
//      switch (version)
//      {
//          case TWO:
//          {
//             ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1119" + path);
//             request.body(mediaType, payload, payload.getClass(), null, annotations);
//             ClientResponse<T> response = request.post();
//             T entity = null;
//             if (genericReturnType != null)
//             {
//                entity = response.getEntity(returnType, genericReturnType.getType());
//             }
//             else
//             {
//                entity = response.getEntity(returnType);
//             }
//           
//             return entity;
//          }
//          
//          case THREE:
//          {
//             Client client = ClientBuilder.newClient();
//             WebTarget target = client.target("http://localhost:8080/RESTEASY-1119" + path);
//             Entity<S> entity = Entity.entity(payload, mediaType, annotations);
//             Response response = target.request().post(entity);
//             System.out.println("status: " + response.getStatus());
//             Assert.assertEquals(200, response.getStatus());
//             T result = null;
//             if (genericReturnType != null)
//             {
//                result = response.readEntity(genericReturnType);  
//             }
//             else
//             {
//                result = response.readEntity(returnType);
//             }
//             return result;
//          }
//
//          default:
//             throw new Exception("Unknown version: " + version);
//      }
//   }
   
   @SuppressWarnings({"unchecked"})
   <S, T> T post(Version version, String path, S payload, MediaType mediaType, Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception
   {
      switch (version)
      {
          case TWO:
          {
             ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1119" + path);
             request.body(mediaType, payload, payload.getClass(), null, annotations);
             ClientResponse<T> response = request.post();
             T entity = null;
             if (genericReturnType != null)
             {
                entity = response.getEntity(returnType, genericReturnType);
             }
             else
             {
                entity = response.getEntity(returnType);
             }
           
             return entity;
          }
          
          case THREE:
          {
             Client client = ClientBuilder.newClient();
             WebTarget target = client.target("http://localhost:8080/RESTEASY-1119" + path);
             Entity<S> entity = Entity.entity(payload, mediaType, annotations);
             Response response = target.request().post(entity);
             System.out.println("status: " + response.getStatus());
             Assert.assertEquals(200, response.getStatus());
             T result = null;
             if (genericReturnType != null)
             {
                result = response.readEntity(new GenericType<T>(genericReturnType));  
             }
             else
             {
                result = response.readEntity(returnType);
             }
             return result;
          }

          default:
             throw new Exception("Unknown version: " + version);
      }
   }
   
//   @SuppressWarnings({"unchecked"})
//   <S, T, U> T post(Version version, String path, S payload, MediaType mediaType, Class<T> returnType, GenericTypeWrapper<U> genericReturnType, Annotation[] annotations) throws Exception
//   {
//      switch (version)
//      {
//          case TWO:
//          {
//             ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1119" + path);
//             request.body(mediaType, payload, payload.getClass(), null, annotations);
//             ClientResponse<T> response = request.post();
//             T entity = null;
//             if (genericReturnType != null)
//             {
//                entity = response.getEntity(returnType, genericReturnType.version2());
//             }
//             else
//             {
//                entity = response.getEntity(returnType);
//             }
//           
//             return entity;
//          }
//          
//          case THREE:
//          {
//             Client client = ClientBuilder.newClient();
//             WebTarget target = client.target("http://localhost:8080/RESTEASY-1119" + path);
//             Entity<S> entity = Entity.entity(payload, mediaType, annotations);
//             Response response = target.request().post(entity);
//             System.out.println("status: " + response.getStatus());
//             Assert.assertEquals(200, response.getStatus());
//             T result = null;
//             if (genericReturnType != null)
//             {
//                result = response.readEntity(genericReturnType.version3());  
//             }
//             else
//             {
//                result = response.readEntity(returnType);
//             }
//             return result;
//          }
//
//          default:
//             throw new Exception("Unknown version: " + version);
//      }
//   }

   static class GenericTypeWrapper<T>
   {  
      private org.jboss.resteasy.util.GenericType<T> v2;
      private javax.ws.rs.core.GenericType<T> v3;
      
      public GenericTypeWrapper(org.jboss.resteasy.util.GenericType<T> v2, javax.ws.rs.core.GenericType<T> v3)
      {
         this.v2 = v2;
         this.v3 = v3;
      }
      
      public Type version2()
      {
         return v2.getGenericType();
      }
      
      public javax.ws.rs.core.GenericType<T> version3()
      {
         return v3;
      }
   }
}
