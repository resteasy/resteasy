/**
 *
 */
package org.jboss.resteasy.test.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.LocateTestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.activation.DataHandler;
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
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestMimeMultipartProvider extends BaseResourceTest
{

   private static final Logger logger = Logger
           .getLogger(TestMimeMultipartProvider.class);

   private static final String TEST_URI = generateURL("/mime");

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      logger.debug("Starting up...");
      dispatcher.getRegistry().addPerRequestResource(
              SimpleMimeMultipartResource.class);
   }

   @Test
   public void testPutForm() throws Exception
   {
      MultipartFormDataOutput mpfdo = new MultipartFormDataOutput();
      mpfdo.addFormData("part1", "This is Value 1", MediaType.TEXT_PLAIN_TYPE);
      mpfdo.addFormData("part2", "This is Value 2", MediaType.TEXT_PLAIN_TYPE);
      mpfdo.addFormData("data.txt",  LocateTestData.getTestData("data.txt"), MediaType.TEXT_PLAIN_TYPE);
      ClientRequest request = new ClientRequest(TEST_URI);
      request.body(MediaType.MULTIPART_FORM_DATA_TYPE, mpfdo);
      ClientResponse<String> response = request.put(String.class);
      Assert.assertEquals(200, response.getStatus());
      String responseBody = response.getEntity();
      Assert.assertEquals(responseBody, "Count: 3");      
   }

   @Test
   public void testPut() throws Exception
   {
      MultipartOutput mpo = new MultipartOutput();
      mpo.addPart("This is Value 1", MediaType.TEXT_PLAIN_TYPE);
      mpo.addPart("This is Value 2", MediaType.TEXT_PLAIN_TYPE);
      mpo.addPart(LocateTestData.getTestData("data.txt"), MediaType.TEXT_PLAIN_TYPE);
      ClientRequest request = new ClientRequest(TEST_URI);
      request.body(MediaType.MULTIPART_FORM_DATA_TYPE, mpo);
      ClientResponse<String> response = request.put(String.class);
      Assert.assertEquals(200, response.getStatus());
      String responseBody = response.getEntity();
      Assert.assertEquals(responseBody, "Count: 3");      
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

   private void testMultipart(String uri) throws Exception
   {
      MultipartFormDataOutput mpfdo = new MultipartFormDataOutput();
      mpfdo.addFormData("bill", createCustomerData("bill"), MediaType.APPLICATION_XML_TYPE);
      mpfdo.addFormData("monica", createCustomerData("monica"), MediaType.APPLICATION_XML_TYPE);
      ClientRequest request = new ClientRequest(uri);
      request.body(MediaType.MULTIPART_FORM_DATA_TYPE, mpfdo);
      ClientResponse<?> response = request.put();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
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

      @Path("related")
      @PUT
      @Consumes(MultipartConstants.MULTIPART_RELATED)
      public void putRelated(MultipartRelatedOutput output);

      @Path("mixed")
      @PUT
      @Consumes("multipart/mixed")
      public void putMixedList(
              @PartType("application/xml") List<Customer> customers);

      @Path("form")
      @PUT
      @Consumes("multipart/form-data")
      public void putFormDataMap(
              @PartType("application/xml") Map<String, Customer> customers);

      @Path("form/class")
      @PUT
      @Consumes("multipart/form-data")
      public void putFormDataMap(
              @MultipartForm SimpleMimeMultipartResource.Form form);

      @Path("xop")
      @PUT
      @Consumes(MultipartConstants.MULTIPART_RELATED)
      public void putXop(
              @XopWithMultipartRelated SimpleMimeMultipartResource.Xop bean);
   }

   @Test
   public void testMultipartOutput() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class,
              generateBaseUrl());
      MultipartOutput output = new MultipartOutput();
      output.addPart(new Customer("bill"), MediaType.APPLICATION_XML_TYPE);
      output.addPart(new Customer("monica"), MediaType.APPLICATION_XML_TYPE);
      client.putMixed(output);
   }

   @Test
   public void testMultipartFormDataOutput() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class,
              generateBaseUrl());
      MultipartFormDataOutput output = new MultipartFormDataOutput();
      output.addFormData("bill", new Customer("bill"),
              MediaType.APPLICATION_XML_TYPE);
      output.addFormData("monica", new Customer("monica"),
              MediaType.APPLICATION_XML_TYPE);
      client.putFormData(output);
   }

   @Test
   public void testMultipartRelatedOutput() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class,
              generateBaseUrl());
      MultipartRelatedOutput output = new MultipartRelatedOutput();
      output.setStartInfo("text/xml");

      Map<String, String> mediaTypeParameters = new LinkedHashMap<String, String>();
      mediaTypeParameters.put("charset", "UTF-8");
      mediaTypeParameters.put("type", "text/xml");
      output
              .addPart(
                      "<m:data xmlns:m='http://example.org/stuff'>"
                              + "<m:photo><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/me.png'/></m:photo>"
                              + "<m:sig><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/my.hsh'/></m:sig>"
                              + "</m:data>", new MediaType("application",
                              "xop+xml", mediaTypeParameters),
                      "<mymessage.xml@example.org>", "8bit");
      output.addPart("// binary octets for png",
              new MediaType("image", "png"), "<http://example.org/me.png>",
              "binary");
      output.addPart("// binary octets for signature", new MediaType(
              "application", "pkcs7-signature"),
              "<http://example.org/me.hsh>", "binary");
      client.putRelated(output);
   }

   @Test
   public void testMultipartList() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class,
              generateBaseUrl());
      ArrayList<Customer> customers = new ArrayList<Customer>();
      customers.add(new Customer("bill"));
      customers.add(new Customer("monica"));
      client.putMixedList(customers);
   }

   @Test
   public void testMultipartMap() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class,
              generateBaseUrl());
      LinkedHashMap<String, Customer> customers = new LinkedHashMap<String, Customer>();
      customers.put("bill", new Customer("bill"));
      customers.put("monica", new Customer("monica"));
      client.putFormDataMap(customers);
   }

   @Test
   public void testMultipartForm() throws Exception
   {
      MultipartClient client = ProxyFactory.create(MultipartClient.class,
              generateBaseUrl());
      SimpleMimeMultipartResource.Form form = new SimpleMimeMultipartResource.Form(
              new Customer("bill"), new Customer("monica"));
      client.putFormDataMap(form);
   }

   @Test
   public void testXop() throws Exception
   {
      logger.info("System encoding: " + System.getProperty("file.encoding"));
      MultipartClient client = ProxyFactory.create(MultipartClient.class,
              generateBaseUrl());
      SimpleMimeMultipartResource.Xop xop = new SimpleMimeMultipartResource.Xop(
              new Customer("bill\u00E9"), new Customer("monica"),
              "Hello Xop World!".getBytes("UTF-8"), new DataHandler(
                      new ByteArrayDataSource("Hello Xop World!"
                              .getBytes("UTF-8"),
                              MediaType.APPLICATION_OCTET_STREAM)));
      client.putXop(xop);
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
      ClientRequest request = new ClientRequest(TEST_URI);
      ClientResponse<InputStream> response = request.get(InputStream.class);
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus()); 
      BufferedInputStream in = new BufferedInputStream(response.getEntity());
      String contentType = response.getResponseHeaders().getFirst("content-type");
      ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
      MimeMultipart mimeMultipart = new MimeMultipart(ds);
      Assert.assertEquals(mimeMultipart.getCount(), 2);
      response.releaseConnection();
   }

   @Test
   public void testFile() throws Exception
   {
      ClientRequest request = new ClientRequest(TEST_URI + "/file/test");
      request
              .body(
                      "multipart/form-data; boundary=---------------------------52524491016334132001492192799",
                      form);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());

   }

   private static final String form = "-----------------------------52524491016334132001492192799\r\n"
           + "Content-Disposition: form-data; name=\"submit-name\"\r\n"
           + "\r\n"
           + "Bill\r\n"
           + "-----------------------------52524491016334132001492192799\r\n"
           + "Content-Disposition: form-data; name=\"files\"; filename=\"stuff.txt\"\r\n"
           + "Content-Type: text/plain\r\n"
           + "\r\n"
           + "hello world\r\n"
           + "\r\n"
           + "-----------------------------52524491016334132001492192799--";
   // }
}
