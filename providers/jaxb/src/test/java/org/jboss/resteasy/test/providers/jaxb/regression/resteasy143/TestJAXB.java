package org.jboss.resteasy.test.providers.jaxb.regression.resteasy143;

import static org.jboss.resteasy.test.TestPortProvider.*;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestJAXB extends BaseResourceTest
{

   @Override
   @Before
   public void before() throws Exception
   {
      addPerRequestResource(StoreResource.class, AbstractData.class, DataCollectionPackage.class, DataCollectionRecord.class, ObjectFactory.class);
      super.before();
   }

   private static final String XML_CONTENT = "<ns:DataCollectionPackage xmlns:ns=\"http://www.example.org/DataCollectionPackage\">\n"
           + "  <sourceID>System A</sourceID>\n"
           + "  <eventID>Exercise B</eventID>\n"
           + "  <dataRecords>\n"
           + "     <DataCollectionRecord>\n"
           + "        <timestamp>2008-08-13T12:24:00</timestamp>\n"
           + "        <collectedData>Operator pushed easy button</collectedData>\n"
           + "     </DataCollectionRecord>\n"
           + "  </dataRecords>\n"
           + "</ns:DataCollectionPackage>";

   /*
   * I can't get this to work
   *
   * @Test public void testJAXB() throws Exception { //JAXBContext context =
   * JAXBContext.newInstance(DataCollectionPackage.class,
   * ObjectFactory.class); //JAXBContext context =
   * JAXBContext.newInstance(DataCollectionPackage.class); JAXBContext context
   * =
   * JAXBContext.newInstance(DataCollectionPackage.class.getPackage().getName
   * ()); //JAXBElement element =
   * (JAXBElement)context.createUnmarshaller().unmarshal(new StreamSource(new
   * StringReader(XML_CONTENT_DEFAULT_NS)), DataCollectionPackage.class);
   * JAXBElement element =
   * (JAXBElement)context.createUnmarshaller().unmarshal(new
   * StringReader(XML_CONTENT_DEFAULT_NS));
   *
   *
   *
   * DataCollectionPackage pkg = (DataCollectionPackage)element.getValue();
   * Assert.assertNotNull(pkg.getSourceID());
   *
   * context =
   * JAXBContext.newInstance(DataCollectionPackage.class.getPackage()
   * .getName());
   *
   * pkg = new DataCollectionPackage(); pkg.setSourceID("System A");
   * pkg.setEventID("Exercise B"); DataCollectionPackage.DataRecords records =
   * new DataCollectionPackage.DataRecords(); DataCollectionRecord record =
   * new DataCollectionRecord(); record.setCollectedData("Operator pushed");
   * record
   * .setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(
   * "2008-08-13T12:24:00"));
   *
   * records.getDataCollectionRecord().add(record);
   * pkg.setDataRecords(records);
   *
   * StringWriter writer = new StringWriter();
   * context.createMarshaller().marshal(new
   * ObjectFactory().createDataCollectionPackage(pkg), writer);
   * System.out.println(writer.toString()); }
   */

   @Test
   public void testWire() throws Exception
   {
      {
         ClientRequest request = new ClientRequest(generateURL("/storeXML"));
         request.body("application/xml", XML_CONTENT);
         ClientResponse<?> response = request.post();
         Assert.assertEquals(201, response.getStatus());
         response.releaseConnection();
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/storeXML/abstract"));
         request.body("application/xml", XML_CONTENT);
         ClientResponse<?> response = request.post();
         Assert.assertEquals(201, response.getStatus());
         response.releaseConnection();
      }
   }
}