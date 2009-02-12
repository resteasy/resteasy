/**
 *
 */
package org.jboss.resteasy.test.providers.multipart;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.LocateTestData;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestMimeMultipartProvider extends BaseResourceTest
{

   private static final Logger logger = LoggerFactory.getLogger(TestMimeMultipartProvider.class);

   private static final String TEST_URI = generateURL("/mime");

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      logger.debug("Starting up...");
      dispatcher.getRegistry().addPerRequestResource(SimpleMimeMultipartResource.class);
   }

   @Test
   public void testPut() throws Exception
   {
      HttpClient client = new HttpClient();
      List<Part> partsList = new ArrayList<Part>();
      partsList.add(new StringPart("part1", "This is Value 1"));
      partsList.add(new StringPart("part2", "This is Value 2"));
      partsList.add(new FilePart("data.txt", LocateTestData.getTestData("data.txt")));
      Part[] parts = partsList.toArray(new Part[partsList.size()]);
      PutMethod method = new PutMethod(TEST_URI);
      RequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
      method.setRequestEntity(entity);
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      String responseBody = method.getResponseBodyAsString();
      Assert.assertEquals(responseBody, "Count: 3");
      method.releaseConnection();
   }

   @Test
   public void testForm() throws Exception
   {

      testMultipart(TEST_URI + "/form");
      testMultipart(TEST_URI + "/form/map");
      testMultipart(TEST_URI + "/form/class");
      testMultipart(TEST_URI + "/multi");
      testMultipart(TEST_URI + "/multi/list");
   }

   private void testMultipart(String uri) throws JAXBException, IOException
   {
      HttpClient client = new HttpClient();
      List<Part> partsList = new ArrayList<Part>();
      StringPart part = new StringPart("bill", createCustomerData("bill"));
      part.setContentType("application/xml");
      partsList.add(part);
      StringPart part1 = new StringPart("monica", createCustomerData("monica"));
      part1.setContentType("application/xml");
      partsList.add(part1);
      Part[] parts = partsList.toArray(new Part[partsList.size()]);
      PutMethod method = new PutMethod(uri);
      RequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
      method.setRequestEntity(entity);
      int status = client.executeMethod(method);
      Assert.assertEquals(204, status);
      method.releaseConnection();
   }

   @Path("mime")
   public static interface MultipartClient
   {
      @Path("mixed")
      @PUT
      @Consumes("multipart/mixed")
      public void putMixed(MultipartOutput output);

      @Path("form")
      @PUT
      @Consumes("multipart/form-data")
      public void putFormData(MultipartFormDataOutput output);

      @Path("mixed")
      @PUT
      @Consumes("multipart/mixed")
      public void putMixedList(@PartType("application/xml") List<Customer> customers);

      @Path("form")
      @PUT
      @Consumes("multipart/form-data")
      public void putFormDataMap(@PartType("application/xml") Map<String, Customer> customers);

      @Path("form")
      @PUT
      @Consumes("multipart/form-data")
      public void putFormDataMap(@MultipartForm SimpleMimeMultipartResource.Form form);
   }

   @Test
   public void testMultipartOutput() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class, generateBaseUrl());
      MultipartOutput output = new MultipartOutput();
      output.addPart(new Customer("bill"), MediaType.APPLICATION_XML_TYPE);
      output.addPart(new Customer("monica"), MediaType.APPLICATION_XML_TYPE);
      client.putMixed(output);
   }

   @Test
   public void testMultipartFormDataOutput() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class, generateBaseUrl());
      MultipartFormDataOutput output = new MultipartFormDataOutput();
      output.addFormData("bill", new Customer("bill"), MediaType.APPLICATION_XML_TYPE);
      output.addFormData("monica", new Customer("monica"), MediaType.APPLICATION_XML_TYPE);
      client.putFormData(output);
   }

   @Test
   public void testMultipartList() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class, generateBaseUrl());
      ArrayList<Customer> customers = new ArrayList<Customer>();
      customers.add(new Customer("bill"));
      customers.add(new Customer("monica"));
      client.putMixedList(customers);
   }

   @Test
   public void testMultipartMap() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class, generateBaseUrl());
      LinkedHashMap<String, Customer> customers = new LinkedHashMap<String, Customer>();
      customers.put("bill", new Customer("bill"));
      customers.put("monica", new Customer("monica"));
      client.putFormDataMap(customers);
   }

   @Test
   public void testMultipartForm() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class, generateBaseUrl());
      SimpleMimeMultipartResource.Form form = new SimpleMimeMultipartResource.Form(new Customer(
              "bill"), new Customer("monica"));
      client.putFormDataMap(form);
   }

   private String createCustomerData(String name) throws JAXBException
   {
      JAXBContext context = JAXBContext.newInstance(Customer.class);
      StringWriter writer = new StringWriter();
      context.createMarshaller().marshal(new Customer(name), writer);
      String data = writer.toString();
      return data;
   }

   @Test
   public void testGet() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(TEST_URI);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);
      InputStream response = method.getResponseBodyAsStream();
      BufferedInputStream in = new BufferedInputStream(response);
      String contentType = method.getResponseHeader("content-type").getValue();
      ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
      MimeMultipart mimeMultipart = new MimeMultipart(ds);
      Assert.assertEquals(mimeMultipart.getCount(), 2);
      method.releaseConnection();
   }

   @Test
   public void testFile() throws Exception
   {
      ClientRequest request = new ClientRequest(TEST_URI + "/file/test");
      request.body("multipart/form-data; boundary=---------------------------52524491016334132001492192799", form);
      ClientResponse response = request.post();
      Assert.assertEquals(200, response.getStatus());

   }

   private static final String form =
           "-----------------------------52524491016334132001492192799\r\n" +
                   "Content-Disposition: form-data; name=\"submit-name\"\r\n" +
                   "\r\n" +
                   "Bill\r\n" +
                   "-----------------------------52524491016334132001492192799\r\n" +
                   "Content-Disposition: form-data; name=\"files\"; filename=\"stuff.txt\"\r\n" +
                   "Content-Type: text/plain\r\n" +
                   "\r\n" +
                   "hello world\r\n" +
                   "\r\n" +
                   "-----------------------------52524491016334132001492192799--";

}
