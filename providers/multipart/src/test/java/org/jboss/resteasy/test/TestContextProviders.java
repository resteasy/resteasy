package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.AnnotationLiteral;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

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
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1119
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2015
 */
@SuppressWarnings("deprecation")
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
   
   @XmlRootElement(name = "customer")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Customer
   {
      @XmlElement
      private String name;

      public Customer()
      {
      }

      public Customer(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }
   }
   
   @XmlRootElement(name = "name")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Name
   {
      @XmlElement
      private String name;

      public Name()
      {
      }

      public Name(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }
      
      public boolean equals(Object o)
      {
         if (o == null)
            return false;
         if (!(o instanceof Name))
            return false;
         return name.equals(((Name) o).getName());
      }
   }
   
   public static class CustomerForm
   {
      @FormParam("customer")
      @PartType("application/xml")
      private Customer customer;

      public Customer getCustomer() { return customer; }
      public void setCustomer(Customer cust) { this.customer = cust; }
   }
   
   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Xop
   {
     @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
     private byte[] bytes;

     public Xop(byte[] bytes)
     {
        this.bytes = bytes;
     }
     
     public Xop()
     {
     }
     
     public byte[] getBytes()
     {
        return bytes;
     }
     
     public void setBytes(byte[] bytes)
     {
        this.bytes = bytes;
     }
   }
   
   @Path("")
   public static class TestResource
   {  
      @GET
      @Produces("multipart/mixed")
      @Path("get/mixed")
      public MultipartOutput getMixed()
      {
         System.out.println("entering getMixed()");
         MultipartOutput output = new MultipartOutput();
         output.addPart(new Customer("Bill"), MediaType.APPLICATION_XML_TYPE);
         output.addPart("Bob", MediaType.TEXT_PLAIN_TYPE);
         return output;
      }
    
      @GET
      @Produces("multipart/form-data")
      @MultipartForm
      @Path("get/form")
      public MultipartFormDataOutput getForm()
      {
         System.out.println("entering getForm()");
         MultipartFormDataOutput output = new MultipartFormDataOutput();
         output.addFormData("bill", new Customer("Bill"), MediaType.APPLICATION_XML_TYPE, "tmp1");
         output.addFormData("bob", "Bob", MediaType.TEXT_PLAIN_TYPE);
         return output;
      }
      
      @GET
      @Produces("multipart/mixed")
      @PartType("application/xml")
      @Path("get/list")
      public List<Customer> getList()
      {
         System.out.println("entering getList()");
         List<Customer> list = new ArrayList<Customer>();
         list.add(new Customer("Bill"));
         list.add(new Customer("Bob"));
         return list;
      }
      
      @GET
      @Produces("multipart/form-data")
      @PartType("application/xml")
      @Path("get/map")
      public Map<String, Customer> getMap()
      {
         System.out.println("entering getMap()");
         Map<String, Customer> map = new HashMap<String, Customer>();
         map.put("bill", new Customer("Bill"));
         map.put("bob", new Customer("Bob"));
         return map;
      }
      
      @GET
      @Produces("multipart/related")
      @Path("get/related")
      public MultipartRelatedOutput getRelated()
      {
         System.out.println("entering getRelated()");
         MultipartRelatedOutput output = new MultipartRelatedOutput();
         output.setStartInfo("text/html");
         output.addPart("Bill", new MediaType("image", "png"), "bill", "binary");
         output.addPart("Bob", new MediaType("image", "png"), "bob", "binary");
         return output;
      }
      
      @GET
      @Path("get/multipartform")
      @Produces("multipart/form-data")
      @MultipartForm
      public CustomerForm getMultipartForm()
      {
         CustomerForm form = new CustomerForm();
         form.setCustomer(new Customer("Bill"));
         return form;
      }
      
      @GET
      @Path("get/xop")
      @Produces("multipart/related")
      @XopWithMultipartRelated
      public Xop getXop()
      {
         return new Xop("goodbye world".getBytes());
      }
      
      @POST
      @Consumes("multipart/mixed")
      @Produces(MediaType.APPLICATION_XML)
      @Path("post/mixed")
      public List<Name> postMixed(MultipartInput input) throws IOException
      {
         System.out.println("entering getMixed()");
         List<InputPart> list = input.getParts();
         List<Name> names = new ArrayList<Name>();
         for (Iterator<InputPart> it = list.iterator(); it.hasNext(); )
         {
            InputPart inputPart = it.next();
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType()))
            {
               names.add(new Name(inputPart.getBody(Customer.class, null).getName()));
            }
            else
            {
               names.add(new Name(inputPart.getBody(String.class, null)));
            }
         }
         return names;
      }
      
      @POST
      @Consumes("multipart/form-data")
      @Produces(MediaType.APPLICATION_XML)
      @Path("post/form")
      public List<Name> postForm(MultipartFormDataInput input) throws IOException
      {
         System.out.println("entering postForm()");
         Map<String, List<InputPart>> map = input.getFormDataMap();
         List<Name> names = new ArrayList<Name>();
         for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();
            InputPart inputPart = map.get(key).iterator().next();
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType()))
            {
               names.add(new Name(inputPart.getBody(Customer.class, null).getName()));
            }
            else
            {
               names.add(new Name(inputPart.getBody(String.class, null)));
            }
         }
         return names;
      }
      
      @POST
      @Consumes("multipart/mixed")
      @Produces(MediaType.APPLICATION_XML)
      @Path("post/list")
      public List<Name> postList(List<Customer> customers) throws IOException
      {
         System.out.println("entering postList()");
         List<Name> names = new ArrayList<Name>();
         for (Customer customer : customers)
         {
            names.add(new Name(customer.getName()));
         }
         return names;
      }
      
      @POST
      @Consumes("multipart/form-data")
      @Produces(MediaType.APPLICATION_XML)
      @Path("post/map")
      public List<Name> postMap(Map<String, Customer> customers) throws IOException
      {
         System.out.println("entering postMap()");
         List<Name> names = new ArrayList<Name>();
         for (Iterator<String> it = customers.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();
            Customer customer = customers.get(key);
            names.add(new Name(key + ":" + customer.getName()));
         }
         return names;
      }
      
      @POST
      @Consumes("multipart/related")
      @Produces(MediaType.APPLICATION_XML)
      @Path("post/related")
      public List<Name> postRelated(MultipartRelatedInput customers) throws IOException
      {
         System.out.println("entering postMap()");
         List<Name> names = new ArrayList<Name>();
         for (Iterator<InputPart> it = customers.getParts().iterator(); it.hasNext(); )
         {
            InputPart part = it.next();
            String name = part.getBody(String.class, null);
            names.add(new Name(name));
         }
         return names;
      }
      
      @POST
      @Consumes("multipart/form-data")
      @Path("post/multipartform")
      public String postMultipartForm(@MultipartForm CustomerForm form) throws IOException
      {
         System.out.println("entering postMultipartForm()");
         return form.getCustomer().getName();
      }
      
      @POST
      @Path("post/xop")
      @Consumes("multipart/related")
      public String postXop(@XopWithMultipartRelated Xop xop)
      {
         return new String(xop.getBytes());
      }
   }
   
   @Before
   public void before() throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
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
            ClientRequest request = new ClientRequest(generateURL(path));
            ClientResponse<T> response = request.get(clazz);
            System.out.println("status: " + response.getStatus());
            Assert.assertEquals(200, response.getStatus());
            T entity = response.getEntity(clazz, null, annotations);
            return entity;
         }
            
         case THREE:
         {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(generateURL(path));
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
   
   @SuppressWarnings({"unchecked"})
   <S, T> T post(Version version, String path, S payload, MediaType mediaType, Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception
   {
      switch (version)
      {
          case TWO:
          {
             ClientRequest request = new ClientRequest(generateURL(path));;
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
             WebTarget target = client.target(generateURL(path));
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
}
