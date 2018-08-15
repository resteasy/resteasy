package org.jboss.resteasy.test.providers.atom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelArchived;
import org.jboss.resteasy.test.providers.atom.resource.AtomAssetMetadata;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelAtomAssetMetadataDecorators;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelAtomAssetMetadtaProcessor;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelCategories;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelCheckinComment;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelCreated;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelDisabled;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelEntryResource;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelFormat;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelNote;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelState;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelUuid;
import org.jboss.resteasy.test.providers.atom.resource.AtomComplexModelVersionNumber;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.DataOutputStream;
import java.io.FilePermission;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.ReflectPermission;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @tpSubChapter Atom provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Check complex model with Atom Provider
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AtomComplexModelTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AtomComplexModelTest.class.getSimpleName());
        war.addClasses(AtomComplexModelArchived.class,
                AtomAssetMetadata.class,
                AtomComplexModelAtomAssetMetadataDecorators.class,
                AtomComplexModelAtomAssetMetadtaProcessor.class,
                AtomComplexModelCategories.class,
                AtomComplexModelCheckinComment.class,
                AtomComplexModelCreated.class,
                AtomComplexModelDisabled.class,
                AtomComplexModelEntryResource.class,
                AtomComplexModelFormat.class,
                AtomComplexModelNote.class,
                AtomComplexModelState.class,
                AtomComplexModelUuid.class,
                AtomComplexModelVersionNumber.class);

        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new FilePermission("<<ALL FILES>>", "read"),
            new ReflectPermission("suppressAccessChecks"),
            new RuntimePermission("accessDeclaredMembers")),
            "permissions.xml");

        return TestUtil.finishContainerPrepare(war, null, AtomComplexModelEntryResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AtomComplexModelTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check complex type
     * @tpSince RESTEasy 3.0.16
     */
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
        assertNotNull("Metadata of complex type is null", atomAssetMetadata);
        assertNotNull("Categories from metadata is missing", atomAssetMetadata.getCategories());
    }

    /**
     * @tpTestDetails Check new client
     * @tpInfo Not for forward compatibility due to 3.1.0.Final, see the migration notes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testNewClient() throws Exception {
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

            assertEquals(HttpResponseCodes.SC_OK, connection.getResponseCode());
        }

        {
            Response response = client.target(generateURL("/entry2")).request()
                    .header("Accept", MediaType.APPLICATION_ATOM_XML)
                    .header("Content-Type", MediaType.APPLICATION_ATOM_XML)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            assertNotNull("Wrong content of response", response.readEntity(Entry.class).getAnyOtherJAXBObject(AtomAssetMetadata.class));
            response.close();
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

            assertEquals(HttpResponseCodes.SC_OK, connection.getResponseCode());
        }

        {
            Response response = client.target(generateURL("/entry4")).request()
                    .header("Accept", MediaType.APPLICATION_XML)
                    .header("Content-Type", MediaType.APPLICATION_XML)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            assertNotNull("Wrong content of response", response.readEntity(Entry.class).getAnyOtherJAXBObject(AtomAssetMetadata.class));
            response.close();
        }
    }
}
