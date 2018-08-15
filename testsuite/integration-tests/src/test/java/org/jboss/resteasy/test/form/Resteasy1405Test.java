package org.jboss.resteasy.test.form;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.junit.Assert.assertEquals;

import java.io.FilePermission;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.test.form.resteasy1405.ByFieldForm;
import org.jboss.resteasy.test.form.resteasy1405.BySetterForm;
import org.jboss.resteasy.test.form.resteasy1405.InputData;
import org.jboss.resteasy.test.form.resteasy1405.MyResource;
import org.jboss.resteasy.test.form.resteasy1405.OutputData;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Injection of @FormParam InputPart fields in @MultipartForm parameters
 * @tpSince RESTEasy 3.1.0
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({NotForForwardCompatibility.class})
public class Resteasy1405Test
{

   @Deployment(testable = false)
   public static Archive<?> createTestArchive()
   {
      WebArchive war = TestUtil.prepareArchive(Resteasy1405Test.class.getSimpleName());
      war.addClasses(ByFieldForm.class, BySetterForm.class, InputData.class, OutputData.class);

      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
          new FilePermission("<<ALL FILES>>", "read"),
          new RuntimePermission("accessDeclaredMembers"),
          new RuntimePermission("getClassLoader")),
          "permissions.xml");

      return TestUtil.finishContainerPrepare(war, null, MyResource.class);
   }

   private JAXBContext jaxbc;

   private Client client;

   @Before
   public void setup() throws JAXBException
   {
      jaxbc = JAXBContext.newInstance(InputData.class);
      client = ClientBuilder.newClient();
   }

   @After
   public void done()
   {
      client.close();
   }
   
   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, Resteasy1405Test.class.getSimpleName());      
   }

   /**
    * @tpTestDetails Injection of Content-type into MultiPartForm with annotated form fields
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testInputPartByField() throws Exception
   {
      WebTarget post = client.target(generateURL("/field"));

      InputData data = new InputData();
      data.setItems(asList("value1", "value2"));

      MultipartFormDataOutput multipart = new MultipartFormDataOutput();
      multipart.addFormData("name", "Test by field", TEXT_PLAIN_TYPE);
      multipart.addFormData("data", asXml(data), APPLICATION_XML_TYPE);
      GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(multipart)
      {
      };

      Response response = post.request().post(Entity.entity(entity, MULTIPART_FORM_DATA_TYPE));
      try
      {
         assertEquals(200, response.getStatus());
         assertEquals("OutputData[name='Test by field', contentType='application/xml', items={value1,value2}]",
               response.readEntity(String.class));
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails Injection of Content-type into MultiPartForm with annotated form setters
    * @tpSince RESTEasy 3.1.0
    */
   @Test
   public void testInputPartBySetter() throws Exception
   {
      WebTarget post = client.target(generateURL("/setter"));

      InputData data = new InputData();
      data.setItems(asList("value1", "value2"));

      MultipartFormDataOutput multipart = new MultipartFormDataOutput();
      multipart.addFormData("name", "Test by setter", TEXT_PLAIN_TYPE);
      multipart.addFormData("data", asXml(data), APPLICATION_XML_TYPE);
      GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(multipart)
      {
      };

      Response response = post.request().post(Entity.entity(entity, MULTIPART_FORM_DATA_TYPE));
      try
      {
         assertEquals(200, response.getStatus());
         assertEquals("OutputData[name='Test by setter', contentType='application/xml', items={value1,value2}]",
               response.readEntity(String.class));
      }
      finally
      {
         response.close();
      }
   }

   private String asXml(Object obj) throws JAXBException
   {
      Marshaller m = jaxbc.createMarshaller();
      m.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());

      StringWriter writer = new StringWriter();
      m.marshal(obj, writer);
      return writer.toString();
   }
}
