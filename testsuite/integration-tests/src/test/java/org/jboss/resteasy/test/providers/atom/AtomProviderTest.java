package org.jboss.resteasy.test.providers.atom;

import java.net.URI;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.atom.resource.AtomProviderCustomer;
import org.jboss.resteasy.test.providers.atom.resource.AtomProviderDataCollectionRecord;
import org.jboss.resteasy.test.providers.atom.resource.AtomProviderResource;
import org.jboss.resteasy.test.providers.atom.resource.ObjectFactory;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Atom provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AtomProviderTest {

    @Path("atom")
    public interface AtomProviderResourceInterface {
        @POST
        @Path("feed")
        @Consumes("application/atom+xml")
        @Produces("application/atom+xml")
        Feed postFeed(String feed);

        @GET
        @Path("xmltype")
        @Produces("application/atom+xml")
        Entry getXmlType();

    }

    protected static final Logger logger = Logger.getLogger(AtomProviderTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AtomProviderTest.class.getSimpleName());
        war.addClass(AtomProviderTest.class);
        return TestUtil.finishContainerPrepare(war, null, AtomProviderResource.class, AtomProviderCustomer.class,
                AtomProviderDataCollectionRecord.class, ObjectFactory.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AtomProviderTest.class.getSimpleName());
    }

    private static final String RFC_COMPLEX_XML = "   <atom:feed xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:ns3=\"http://jboss.org/Customer\">\n"
            + "     <atom:title type=\"text\">dive into mark</atom:title>\n"
            + "     <atom:subtitle type=\"html\">\n"
            + "       A &lt;em&gt;lot&lt;/em&gt; of effort\n"
            + "       went into making this effortless\n"
            + "     </atom:subtitle>\n"
            + "     <atom:updated>2005-07-31T12:29:29Z</atom:updated>\n"
            + "     <atom:id>tag:example.org,2003:3</atom:id>\n"
            + "     <atom:link rel=\"alternate\" type=\"text/html\"\n"
            + "      hreflang=\"en\" href=\"http://example.org/\"/>\n"
            + "     <atom:link rel=\"self\" type=\"application/atom+xml\"\n"
            + "      href=\"http://example.org/feed.atom\"/>\n"
            + "     <atom:rights>Copyright (c) 2003, Mark Pilgrim</atom:rights>\n"
            + "     <atom:generator uri=\"http://www.example.com/\" version=\"1.0\">\n"
            + "       Example Toolkit\n"
            + "     </atom:generator>\n"
            + "     <atom:entry>\n"
            + "       <atom:title>Atom draft-07 snapshot</atom:title>\n"
            + "       <atom:link rel=\"alternate\" type=\"text/html\"\n"
            + "        href=\"http://example.org/2005/04/02/atom\"/>\n"
            + "       <atom:link rel=\"enclosure\" type=\"audio/mpeg\" length=\"1337\"\n"
            + "        href=\"http://example.org/audio/ph34r_my_podcast.mp3\"/>\n"
            + "       <atom:id>tag:example.org,2003:3.2397</atom:id>\n"
            + "       <atom:updated>2005-07-31T12:29:29Z</atom:updated>\n"
            + "       <atom:published>2003-12-13T08:29:29-04:00</atom:published>\n"
            + "       <atom:author>\n"
            + "         <atom:name>Mark Pilgrim</atom:name>\n"
            + "         <atom:uri>http://example.org/</atom:uri>\n"
            + "         <atom:email>f8dy@example.com</atom:email>\n"
            + "       </atom:author>\n"
            + "       <atom:contributor>\n"
            + "         <atom:name>Sam Ruby</atom:name>\n"
            + "       </atom:contributor>\n"
            + "       <atom:contributor>\n"
            + "         <atom:name>Joe Gregorio</atom:name>\n"
            + "       </atom:contributor>\n"
            + "       <atom:content type=\"application/xml\" xml:lang=\"en\"\n"
            + "        xml:base=\"http://diveintomark.org/\" >\n"
            + "         <ns3:customer>\n"
            + "            <name>bill</name>\n"
            + "         </ns3:customer>\n"
            + "       </atom:content>\n" + "     </atom:entry>\n" + "   </atom:feed>";

    public static void assertFeed(Feed feed) throws Exception {
        Assertions.assertEquals("dive into mark", feed.getTitle());
        Assertions.assertTrue(feed.getSubtitle().indexOf("effortless") > -1);
        Assertions.assertEquals(feed.getRights(), "Copyright (c) 2003, Mark Pilgrim");
        Assertions.assertEquals(feed.getGenerator().getUri(), new URI("http://www.example.com/"));
        Assertions.assertEquals(feed.getGenerator().getVersion(), "1.0");
        Assertions.assertEquals(feed.getGenerator().getText().trim(), "Example Toolkit");
        Assertions.assertNotNull(feed.getUpdated());
        Assertions.assertEquals(feed.getId().toString(), "tag:example.org,2003:3");
        Link alternate = feed.getLinkByRel("alternate");
        Assertions.assertNotNull(alternate);
        Assertions.assertEquals(alternate.getType(), MediaType.valueOf("text/html"));
        Assertions.assertEquals(alternate.getHreflang(), "en");
        Assertions.assertEquals(alternate.getHref(), new URI("http://example.org/"));
        Link self = feed.getLinkByRel("self");
        Assertions.assertNotNull(self);
        Assertions.assertEquals(self.getType(), MediaType.APPLICATION_ATOM_XML_TYPE);
        Assertions.assertEquals(self.getHreflang(), null);
        Assertions.assertEquals(self.getHref(), new URI("http://example.org/feed.atom"));

        Assertions.assertEquals(1, feed.getEntries().size());
        Entry entry = feed.getEntries().get(0);
        Assertions.assertEquals("Atom draft-07 snapshot", entry.getTitle());
        alternate = entry.getLinkByRel("alternate");
        Assertions.assertNotNull(alternate);
        Assertions.assertEquals(alternate.getType(), MediaType.valueOf("text/html"));
        Assertions.assertEquals(alternate.getHref(), new URI("http://example.org/2005/04/02/atom"));
        Link enclosure = entry.getLinkByRel("enclosure");
        Assertions.assertNotNull(enclosure);
        Assertions.assertEquals(enclosure.getType(), MediaType.valueOf("audio/mpeg"));
        Assertions.assertEquals(enclosure.getLength(), "1337");
        Assertions.assertEquals(enclosure.getHref(), new URI(
                "http://example.org/audio/ph34r_my_podcast.mp3"));
        Assertions.assertEquals(entry.getId(), new URI("tag:example.org,2003:3.2397"));
        Assertions.assertNotNull(entry.getUpdated());
        Assertions.assertNotNull(entry.getPublished());
        Person author = entry.getAuthors().get(0);
        Assertions.assertEquals(author.getName(), "Mark Pilgrim");
        Assertions.assertEquals(author.getUri(), new URI("http://example.org/"));
        Assertions.assertEquals(author.getEmail(), "f8dy@example.com");
        Assertions.assertEquals(entry.getContributors().get(0).getName(), "Sam Ruby");
        Assertions.assertEquals(entry.getContributors().get(1).getName(), "Joe Gregorio");
        Assertions.assertEquals(entry.getContent().getType(), MediaType.APPLICATION_XML_TYPE);
        Assertions.assertEquals(entry.getContent().getLanguage(), "en");
        Assertions.assertEquals(entry.getContent().getBase(), new URI("http://diveintomark.org/"));
        logger.info(entry.getContent().getElement().getNodeName());
        logger.info(entry.getContent().getElement().getNamespaceURI());
        AtomProviderCustomer cust = entry.getContent().getJAXBObject(AtomProviderCustomer.class);
        Assertions.assertEquals(cust.getName(), "bill");

    }

    /**
     * @tpTestDetails Client sends GET request for atom Feed xml annotated resource. It is asserted that response contains
     *                fields
     *                from original request.
     *                in the second case multiple json entities as String.
     * @tpPassCrit The resource returns json entities in correct format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAtomFeed() throws Exception {
        WebTarget target = client.target(generateURL("/atom/feed"));
        Response response = target.request().get();
        String stringResponse = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        logger.info(stringResponse);

        AtomProviderResourceInterface proxy = client.target(generateURL("")).proxy(AtomProviderResourceInterface.class);
        Feed feed = proxy.postFeed(RFC_COMPLEX_XML);
        assertFeed(feed);
    }

    /**
     * @tpTestDetails Client sends GET request for atom Entry xml annotated resource.
     * @tpPassCrit The response is successful
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAtomEntry() throws Exception {
        WebTarget target = client.target(generateURL("/atom/entry"));
        Response response = target.request().get();
        String stringResponse = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        logger.info(stringResponse);
    }

    /**
     * @tpTestDetails Client sends GET request for atom Entry with xml object.
     * @tpPassCrit The response header contains "application/atom+xml" content-type
     * @tpInfo JBEAP-1048
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHeaderContentType() throws Exception {
        WebTarget target = client.target(generateURL("/atom/xmltype"));
        Response response = target.request().get();
        logger.info(response.getHeaders().getFirst("Content-Type"));
        Assertions.assertEquals("application/atom+xml", response.getHeaders().getFirst("Content-Type"));
    }

    /**
     * @tpTestDetails Client sends GET request for atom Entry with xml object.
     * @tpPassCrit The response entity Entry contains AtomProviderDataCollectionRecord which contains correct value.
     * @tpInfo JBEAP-1048
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlType() throws Exception {
        AtomProviderResourceInterface proxy = client.target(generateURL("")).proxy(AtomProviderResourceInterface.class);
        Entry entry = proxy.getXmlType();
        AtomProviderDataCollectionRecord record = entry.getContent().getJAXBObject(AtomProviderDataCollectionRecord.class);
        Assertions.assertEquals(record.getCollectedData(), "hello world");
    }
}
