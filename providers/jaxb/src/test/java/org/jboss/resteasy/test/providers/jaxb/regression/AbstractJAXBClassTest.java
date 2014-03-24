package org.jboss.resteasy.test.providers.jaxb.regression;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBContext;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextWrapper;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractJAXBClassTest extends BaseResourceTest
{

   @Override
   @Before
   public void before() throws Exception
   {
      addPerRequestResource(SimpleResource.class, Adresse.class, Firmenkunde.class, Kunde.class, Person.class, Privatkunde.class, AbstractJAXBClassTest.class, BaseResourceTest.class);
      super.before();
   }

   @Path("/")
   public static class SimpleResource
   {
      @POST
      public void post(Person person)
      {

         System.out.println("******** HERE ******** " + person.getName() + " " + person.getId());
      }

      @POST
      @Path("kunde")
      public void postKunde(Kunde kunde)
      {
         System.out.println("HERE KUNDE!!!!" + kunde.getNachname());
      }

   }

   private static final String kundeXml = "<?xml version=\"1.0\"?>\n"
           + "<privatkunde>\n"
           + "<nachname>Test</nachname>\n"
           + "<vorname>Theo</vorname>\n"
           + "<seit>2001-01-31T00:00:00+01:00</seit>\n"
           + "<adresse><plz>76133</plz><ort>Karlsruhe</ort><strasse>Moltkestrasse</strasse><hausnr>31</hausnr></adresse>\n"
           + "</privatkunde>";

   @Test
   public void testSeeAlso() throws Exception
   {
      JAXBContext ctx = new JAXBContextWrapper(null, Kunde.class);
      Kunde kunde = (Kunde) ctx.createUnmarshaller().unmarshal(
              new ByteArrayInputStream(kundeXml.getBytes()));
      System.out.println("HERE KUNDE!!!!" + kunde.getNachname());

   }

   /**
    * Test for RESTEASY-126
    *
    * @throws Exception
    */
   @Test
   public void testPost() throws Exception
   {
      {
         try
         {
            ClientRequest request = new ClientRequest(generateURL(""));
            String s = "<?xml version=\"1.0\"?><person><name>bill</name></person>";
            request.body("application/xml", s);
            ClientResponse<?> response = request.post();
            Assert.assertEquals(204, response.getStatus());
            response.releaseConnection();
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }

         {
            try
            {
               ClientRequest request = new ClientRequest(generateURL("/kunde"));
               request.body("application/xml", kundeXml);
               ClientResponse<?> response = request.post();
               Assert.assertEquals(204, response.getStatus());
               response.releaseConnection();
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

}