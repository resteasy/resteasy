package org.jboss.resteasy.resteasy1119;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 10, 2015
 */
@Path("")
public class TestResource
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
