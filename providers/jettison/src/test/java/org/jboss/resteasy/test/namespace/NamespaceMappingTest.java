package org.jboss.resteasy.test.namespace;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.example.a.testcanonical.TestBase;
import org.example.b.test.TestExtends;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NamespaceMappingTest extends BaseResourceTest
{
   @Path("/test/v1")
   public static class TestResourceImpl
   {

      @POST
      @Consumes("application/*+json")
      @Produces("application/*+json")
      @Mapped(namespaceMap = {
              @XmlNsMap(jsonName = "test", namespace = "http://www.example.org/b/Test"),
              @XmlNsMap(jsonName = "can", namespace = "http://www.example.org/a/TestCanonical")
      })
      public TestExtends updateTestExtends(@Mapped(namespaceMap = {
              @XmlNsMap(jsonName = "test", namespace = "http://www.example.org/b/Test"),
              @XmlNsMap(jsonName = "can", namespace = "http://www.example.org/a/TestCanonical")
      }) TestExtends data)
      {
         return data;
      }

      @GET
      @Produces("application/*+json")
      @Mapped(namespaceMap = {
              @XmlNsMap(jsonName = "test", namespace = "http://www.example.org/b/Test"),
              @XmlNsMap(jsonName = "can", namespace = "http://www.example.org/a/TestCanonical")
      })
      public TestExtends getTestExtends()
      {
         TestExtends result = new TestExtends();
         result.setId("12121");
         result.setName("Test");
         result.setDesc("Desc");
         result.setElement2("Test");
         result.setSomeMoreEl("test");
         return result;
      }

      @Path("/manual")
      @Produces("application/*+json")
      @GET
      public String getManual()
      {
         return null;
      }

   }

   static JAXBContext ctx = null;
   static Unmarshaller unmarshaller = null;
   static Marshaller marshaller = null;

   @Override
   @Before
   public void before() throws Exception {
      super.before();
      addPerRequestResource(TestResourceImpl.class);
      ctx = JAXBContext.newInstance("org.example.a.testcanonical:org.example.b.test");
      unmarshaller = ctx.createUnmarshaller();
      marshaller = ctx.createMarshaller();
   }

   @Test
   public void testManual() throws Exception
   {
      String output = marshall();
      System.out.println(output);
      unmarshall(output);
   }

   private String marshall() throws JAXBException
   {
      JAXBContext jc = JAXBContext.newInstance(TestExtends.class, TestBase.class);

      TestExtends result = new TestExtends();
      result.setId("12121");
      result.setName("Test");
      result.setDesc("Desc");
      result.setElement2("Test");
      result.setSomeMoreEl("test");

      Configuration config = new Configuration();
      Map<String, String> xmlToJsonNamespaces = new HashMap<String,String>(1);
      xmlToJsonNamespaces.put("http://www.example.org/b/Test", "test");
      xmlToJsonNamespaces.put("http://www.example.org/a/TestCanonical", "can");
      config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
      MappedNamespaceConvention con = new MappedNamespaceConvention(config);
      StringWriter writer = new StringWriter();
      XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(con, writer);

      Marshaller marshaller = jc.createMarshaller();
      marshaller.marshal(JAXBXmlTypeProvider.wrapInJAXBElement(result, TestExtends.class), xmlStreamWriter);
      return writer.toString();
   }

   private TestExtends unmarshall(String output) throws Exception
   {
      JAXBContext jc = JAXBContext.newInstance("org.example.b.test");
      Configuration config = new Configuration();
      Map<String, String> xmlToJsonNamespaces = new HashMap<String,String>(1);
      xmlToJsonNamespaces.put("http://www.example.org/b/Test", "test");
      xmlToJsonNamespaces.put("http://www.example.org/a/TestCanonical", "can");
      config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
      MappedNamespaceConvention con = new MappedNamespaceConvention(config);
      XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(new JSONObject(output), con);

      Unmarshaller unmarshaller = jc.createUnmarshaller();
      @SuppressWarnings("unchecked")
      JAXBElement<TestExtends> val = (JAXBElement<TestExtends>)unmarshaller.unmarshal(xmlStreamReader);
      return val.getValue();

   }

   @Test
   public void testJsonReqRes() throws Exception
   {
      String getData = getDataFromUrl();
      Assert.assertNotNull(getData);
      System.out.println(getData);
      String postData = postDataToUrl(getData, "application/*+json");
      Assert.assertNotNull(postData);
      new JSONObject(postData);
   }

   private String postDataToUrl(String data, String contentType) throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test/v1"));
      request.body(contentType, data);
      return request.postTarget(String.class);
   }

   private String getDataFromUrl() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test/v1"));
      return request.getTarget(String.class);
   }


}
