package org.jboss.resteasy.test.link;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkTest  extends BaseResourceTest
{
   @Path("/")
   public static class CustomerResource
   {
      @GET
      @Produces("application/xml")
      public Customer getCustomer() {
         Customer cust = new Customer("bill");
         Link link = Link.fromUri("a/b/c").build();
         cust.getLinks().add(link);
         link = Link.fromUri("c/d").rel("delete").build();
         cust.getLinks().add(link);
         return cust;
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(CustomerResource.class);
   }

   @Test
   public void testCustomer() throws Exception
   {
      Client client = ClientBuilder.newClient();
      String str = client.target(generateURL("")).request().get(String.class);
      System.out.println(str);
   }

   @XmlRootElement(name="list")
   public static class LinksList
   {
      protected Link other;

      protected List<Link> links = new ArrayList<Link>();

      @XmlElement(name="link")
      @XmlJavaTypeAdapter(value=Link.JaxbAdapter.class, type=Link.class)
      public List<Link> getLinks()
      {
         return links;
      }

      public void setLinks(List<Link> links)
      {
         this.links = links;
      }

      @XmlElement(name="other")
      @XmlJavaTypeAdapter(value=Link.JaxbAdapter.class)
      public Link getOther()
      {
         return other;
      }

      public void setOther(Link other)
      {
         this.other = other;
      }
   }


   @Test
   public void testLinks() throws Exception
   {
      JAXBContext context = JAXBContext.newInstance(LinksList.class, Link.class, Link.JaxbAdapter.class, Link.JaxbLink.class);
      LinksList list = new LinksList();
      UriBuilder builder = UriBuilder.fromUri("/blah").queryParam("start", "{start}");
      URI uri = builder.build(1);
      System.out.println("uri: " + uri.toString());
      builder.queryParam("start", "{start}");
      builder.queryParam("size", "{size}");
      list.getLinks().add(Link.fromUri(uri).rel("self").build());
      list.getLinks().add(Link.fromUri("/b").rel("father").build());
      list.setOther(Link.fromUri(uri).rel("other").build());

      StringWriter writer = new StringWriter();
      context.createMarshaller().marshal(list, writer);
      String xml = writer.getBuffer().toString();
      System.out.println(xml);

      System.out.println("----");
      list = (LinksList)context.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes()));
      uri = list.getOther().getUri();
      System.out.println(uri.toString());
      context.createMarshaller().marshal(list, System.out);

   }

}
