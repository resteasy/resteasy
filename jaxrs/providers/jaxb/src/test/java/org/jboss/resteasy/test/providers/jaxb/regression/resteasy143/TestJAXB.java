package org.jboss.resteasy.test.providers.jaxb.regression.resteasy143;

import static org.jboss.resteasy.test.TestPortProvider.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple smoke test
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestJAXB {

    private static Dispatcher dispatcher;

    @BeforeClass
    public static void before() throws Exception {
        dispatcher = EmbeddedContainer.start();
        dispatcher.getRegistry().addPerRequestResource(StoreResource.class);
    }

    @AfterClass
    public static void after() throws Exception {
        EmbeddedContainer.stop();
    }

    private static final String XML_CONTENT_DEFAULT_NS = "<DataCollectionPackage xmlns=\"http://www.example.org/DataCollectionPackage\">\n"
            + "  <sourceID>System A</sourceID>\n"
            + "  <eventID>Exercise B</eventID>\n"
            + "  <dataRecords>\n"
            + "     <DataCollectionRecord>\n"
            + "        <timestamp>2008-08-13T12:24:00</timestamp>\n"
            + "        <collectedData>Operator pushed easy button</collectedData>\n"
            + "     </DataCollectionRecord>\n" + "  </dataRecords>\n" + "</DataCollectionPackage>";
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
    public void testWire() throws Exception {

        HttpClient client = new HttpClient();

        {
            PostMethod method = createPostMethod("/storeXML");
            method.setRequestEntity(new StringRequestEntity(XML_CONTENT, "application/xml", null));
            int status = client.executeMethod(method);
            Assert.assertEquals(201, status);
            method.releaseConnection();
        }
        {
            PostMethod method = createPostMethod("/storeXML/abstract");
            method.setRequestEntity(new StringRequestEntity(XML_CONTENT, "application/xml", null));
            int status = client.executeMethod(method);
            Assert.assertEquals(201, status);
            method.releaseConnection();
        }
    }
}