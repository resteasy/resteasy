package org.jboss.resteasy.test.providers.jaxb.collection;

import org.jboss.resteasy.annotations.providers.jaxb.WrappedMap;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.GenericType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MapTest extends BaseResourceTest
{

   @XmlAccessorType(XmlAccessType.FIELD)
   public static class JaxbMap
   {
      @XmlAnyElement
      List<JAXBElement<Entry>> value = new ArrayList<JAXBElement<Entry>>();

      @XmlTransient
      private String entryName;
      @XmlTransient
      private String keyAttributeName;
      @XmlTransient
      private String namespace;

      public JaxbMap()
      {
      }

      public JaxbMap(String entryName, String keyAttributeName, String namespace)
      {
         this.entryName = entryName;
         this.namespace = namespace;
         this.keyAttributeName = keyAttributeName;
      }

      @XmlAccessorType(XmlAccessType.FIELD)
      public static class Entry
      {
         @XmlAnyElement
         Object value;

         @XmlAnyAttribute
         Map<QName, Object> attribute = new HashMap<QName, Object>();

         @XmlTransient
         private String key;

         @XmlTransient
         private String keyAttributeName;

         public Entry()
         {
         }

         public Entry(String keyAttributeName, String key, Object value)
         {
            this.value = value;
            this.keyAttributeName = keyAttributeName;
            setKey(key);
         }

         public Object getValue()
         {
            return value;
         }

         public void setValue(Object value)
         {
            this.value = value;
         }

         public String getKey()
         {
            if (key != null) return key;
            key = (String) attribute.values().iterator().next();
            return key;
         }

         public void setKey(String keyValue)
         {
            this.key = keyValue;
            attribute.clear();

            QName name = new QName(keyAttributeName);
            attribute.put(name, keyValue);
         }
      }

      public void addEntry(String key, Object val)
      {
         Entry entry = new Entry(keyAttributeName, key, val);
         //JAXBElement<Entry> element = new JAXBElement<Entry>(new QName(namespace, entryName, prefix), Entry.class, entry);
         JAXBElement<Entry> element = new JAXBElement<Entry>(new QName(namespace, entryName), Entry.class, entry);
         //JAXBElement<Entry> element = new JAXBElement<Entry>(new QName(entryName), Entry.class, entry);
         value.add(element);
      }

      public List<JAXBElement<Entry>> getValue()
      {
         return value;
      }
   }

   @XmlRootElement(namespace = "http://foo.com")
   public static class Foo
   {
      @XmlAttribute
      private String name;

      public Foo()
      {
      }

      public Foo(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }
   }

   @Path("/map")
   public static class MyResource
   {
      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      public Map<String, Foo> post(Map<String, Foo> map)
      {
         Assert.assertEquals(2, map.size());
         Assert.assertNotNull(map.get("bill"));
         Assert.assertNotNull(map.get("monica"));
         Assert.assertEquals(map.get("bill").getName(), "bill");
         Assert.assertEquals(map.get("monica").getName(), "monica");
         return map;
      }

      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      @Path("/wrapped")
      @WrappedMap(namespace = "")
      public Map<String, Foo> postWrapped(@WrappedMap(namespace = "") Map<String, Foo> map)
      {
         Assert.assertEquals(2, map.size());
         Assert.assertNotNull(map.get("bill"));
         Assert.assertNotNull(map.get("monica"));
         Assert.assertEquals(map.get("bill").getName(), "bill");
         Assert.assertEquals(map.get("monica").getName(), "monica");
         return map;
      }
   }

   @Before
   public void setup()
   {
      addPerRequestResource(MyResource.class);

   }

   @Test
   public void testMap() throws Exception
   {
      JAXBContext ctx = JAXBContext.newInstance(JaxbMap.class, JaxbMap.Entry.class, Foo.class);

      JaxbMap map = new JaxbMap("entry", "key", "http://jboss.org/resteasy");
      map.addEntry("bill", new Foo("hello"));

      JAXBElement<JaxbMap> element = new JAXBElement<JaxbMap>(new QName("http://jboss.org/resteasy", "map", "resteasy"), JaxbMap.class, map);


      StringWriter writer = new StringWriter();
      ctx.createMarshaller().marshal(element, writer);
      String s = writer.toString();
      System.out.println(s);

      ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes());
      StreamSource source = new StreamSource(is);
      JAXBContext ctx2 = JAXBContext.newInstance(JaxbMap.class);
      element = ctx2.createUnmarshaller().unmarshal(source, JaxbMap.class);

      Element entry = (Element) element.getValue().getValue().get(0);

      JAXBContext ctx3 = JAXBContext.newInstance(JaxbMap.Entry.class);
      JAXBElement<JaxbMap.Entry> e = ctx3.createUnmarshaller().unmarshal(entry, JaxbMap.Entry.class);

      System.out.println("hello");

   }


   @Test
   public void testProvider() throws Exception
   {
      String xml = "<resteasy:map xmlns:resteasy=\"http://jboss.org/resteasy\">"
              + "<resteasy:entry key=\"bill\" xmlns=\"http://foo.com\">"
              + "<foo name=\"bill\"/></resteasy:entry>"
              + "<resteasy:entry key=\"monica\" xmlns=\"http://foo.com\">"
              + "<foo name=\"monica\"/></resteasy:entry>"
              + "</resteasy:map>";

      ClientRequest request = new ClientRequest(generateURL("/map"));
      request.body("application/xml", xml);
      ClientResponse<Map<String, Foo>> response = request.post(new GenericType<Map<String, Foo>>()
      {
      });
      Assert.assertEquals(200, response.getStatus());
      Map<String, Foo> map = response.getEntity();
      Assert.assertEquals(2, map.size());
      Assert.assertNotNull(map.get("bill"));
      Assert.assertNotNull(map.get("monica"));
      Assert.assertEquals(map.get("bill").getName(), "bill");
      Assert.assertEquals(map.get("monica").getName(), "monica");

      request = new ClientRequest(generateURL("/map"));
      request.body("application/xml", xml);
      ClientResponse<String> response2 = request.post(String.class);
      System.out.println(response2.getEntity());


   }

   @Test
   public void testWrapped() throws Exception
   {
      String xml = "<map xmlns:foo=\"http://foo.com\">"
              + "<entry key=\"bill\">"
              + "<foo:foo name=\"bill\"/></entry>"
              + "<entry key=\"monica\">"
              + "<foo:foo name=\"monica\"/></entry>"
              + "</map>";


      ClientRequest request = new ClientRequest(generateURL("/map/wrapped"));
      request.body("application/xml", xml);
      ClientResponse<String> response2 = request.post(String.class);
      Assert.assertEquals(200, response2.getStatus());
      System.out.println(response2.getEntity());

      request = new ClientRequest(generateURL("/map/wrapped"));
      request.body("application/xml", xml);
      ClientResponse<Map<String, Foo>> response = request.post(new GenericType<Map<String, Foo>>()
      {
      });
      Assert.assertEquals(200, response.getStatus());
      Map<String, Foo> map = response.getEntity();
      Assert.assertEquals(2, map.size());
      Assert.assertNotNull(map.get("bill"));
      Assert.assertNotNull(map.get("monica"));
      Assert.assertEquals(map.get("bill").getName(), "bill");
      Assert.assertEquals(map.get("monica").getName(), "monica");

   }

   @Test
   public void testBadWrapped() throws Exception
   {
      String xml = "<resteasy:map xmlns:resteasy=\"http://jboss.org/resteasy\">"
              + "<resteasy:entry key=\"bill\" xmlns=\"http://foo.com\">"
              + "<foo name=\"bill\"/></resteasy:entry>"
              + "<resteasy:entry key=\"monica\" xmlns=\"http://foo.com\">"
              + "<foo name=\"monica\"/></resteasy:entry>"
              + "</resteasy:map>";

      ClientRequest request = new ClientRequest(generateURL("/map/wrapped"));
      request.body("application/xml", xml);
      ClientResponse<String> response2 = request.post(String.class);
      Assert.assertEquals(400, response2.getStatus());

   }
}
