package org.jboss.resteasy.test.providers.atom.jaxb.extended.testcases;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.providers.atom.jaxb.extended.resources.AtomAssetMetadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.DataOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * 10 18 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class SmokeTest extends BaseResourceTest {

    @Test
    public void testComplexType() throws Exception {

        URI baseUri = new URI("resteasy-test");

        Entry entry = new Entry();
        entry.setTitle("testtitle");
        entry.setSummary("testdesc");
        entry.setPublished(new Date());
        entry.getAuthors().add(new Person("testperson"));
        entry.setId(baseUri);

        AtomAssetMetadata atomAssetMetadata = entry.getAnyOtherJAXBObject(AtomAssetMetadata.class);
        if (atomAssetMetadata == null) {
            atomAssetMetadata = new AtomAssetMetadata();
        }
        atomAssetMetadata.setArchived(false);
        atomAssetMetadata.setUuid("testuuid");
        atomAssetMetadata.setCategories(new String[]{"a", "b", "c"});

        entry.setAnyOtherJAXBObject(atomAssetMetadata);

        Content content = new Content();
        content.setSrc(UriBuilder.fromUri(baseUri).path("binary").build());
        content.setType(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        entry.setContent(content);


        Class[] classes = new Class[]{AtomAssetMetadata.class, Entry.class};
        JAXBContext jaxbContext = JAXBContext.newInstance(classes);


        Marshaller marshaller = jaxbContext.createMarshaller();

        Writer xmlWriter = new StringWriter();
        marshaller.marshal(entry, xmlWriter);
        String xmlOut = xmlWriter.toString();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader xmlReader = new StringReader(xmlOut);

        Entry newEntry = (Entry) unmarshaller.unmarshal(xmlReader);
        atomAssetMetadata = newEntry.getAnyOtherJAXBObject(AtomAssetMetadata.class);
        assertNotNull(atomAssetMetadata);
        assertNotNull(atomAssetMetadata.getCategories());

    }

    @Test
    public void testClient() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<entry xmlns=\"http://www.w3.org/2005/Atom\">" +
                "<title>testCreatePackageFromAtom7</title>" +
                "<summary>desc for testCreatePackageFromAtom</summary>" +
                "<metadata xmlns=\"\"><categories><value>c1</value></categories> <note><value>meta</value> </note></metadata>" +
                "</entry>";

        {
            URL url = new URL(generateURL("/entry"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_ATOM_XML);
            connection.setRequestProperty("Content-Length", Integer.toString(xml.getBytes().length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();

            assertEquals(200, connection.getResponseCode());
        }

        {

            ClientRequest request = new ClientRequest(generateURL("/entry2"));
            request.header("Accept", MediaType.APPLICATION_ATOM_XML);
            request.header("Content-Type", MediaType.APPLICATION_ATOM_XML);
            ClientResponse<Entry> response = request.get(Entry.class);
            Assert.assertEquals(200, response.getStatus());

            assertNotNull(response.getEntity().getAnyOtherJAXBObject(AtomAssetMetadata.class));
        }

        {
            URL url = new URL(generateURL("/entry3"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", MediaType.APPLICATION_XML);
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_XML);
            connection.setRequestProperty("Content-Length", Integer.toString(xml.getBytes().length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(xml);
            wr.flush();
            wr.close();

            assertEquals(200, connection.getResponseCode());
        }


        {

            ClientRequest request = new ClientRequest(generateURL("/entry4"));
            request.header("Accept", MediaType.APPLICATION_XML);
            request.header("Content-Type", MediaType.APPLICATION_XML);
            ClientResponse<Entry> response = request.get(Entry.class);
            Assert.assertEquals(200, response.getStatus());

            assertNotNull(response.getEntity().getAnyOtherJAXBObject(AtomAssetMetadata.class));
        }


    }

    @Before
    public void setUp() throws Exception {
        dispatcher.getRegistry().addPerRequestResource(EntryResource.class);
    }


}
