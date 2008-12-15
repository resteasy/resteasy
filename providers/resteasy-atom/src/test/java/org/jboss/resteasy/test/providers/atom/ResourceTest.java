package org.jboss.resteasy.test.providers.atom;

import static org.jboss.resteasy.test.TestPortProvider.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceTest extends BaseResourceTest {
    @Path("atom")
    public static class AtomServer {
        @GET
        @Path("entry")
        @Produces("application/atom+xml")
        public Entry getEntry() {
            Entry entry = new Entry();
            entry.setTitle("Hello World");
            Content content = new Content();
            content.setJAXBObject(new Customer("bill"));
            entry.setContent(content);
            return entry;
        }

        @GET
        @Path("feed")
        @Produces("application/atom+xml")
        public Feed getFeed() {
            Feed feed = new Feed();
            feed.getEntries().add(getEntry());
            return feed;
        }

        @GET
        @Path("text/entry")
        @Produces("application/atom+xml")
        public Entry getTextEntry() {
            Entry entry = new Entry();
            entry.setTitle("Hello World");
            Content content = new Content();
            content.setText("<pre>How are you today?\nNotBad!</pre>");
            content.setType(MediaType.TEXT_HTML_TYPE);
            entry.setContent(content);
            return entry;
        }

        @GET
        @Path("text/feed")
        @Produces("application/atom+xml")
        public Feed getTextFeed() {
            Feed feed = new Feed();
            feed.getEntries().add(getTextEntry());
            return feed;
        }

        @POST
        @Path("feed")
        @Consumes("application/atom+xml")
        @Produces("application/atom+xml")
        public Feed postFeed(Feed feed) throws Exception {
            assertFeed(feed);
            return feed;
        }

        @GET
        @Path("xmltype")
        @Produces("application/atom+xml")
        public Entry getXmlType() {
            Entry entry = new Entry();
            entry.setTitle("Hello World");
            Content content = new Content();
            DataCollectionRecord record = new DataCollectionRecord();
            record.setCollectedData("hello world");
            content.setJAXBObject(record);
            entry.setContent(content);
            return entry;

        }
    }

    @Path("atom")
    public static interface AtomServerInterface {
        @POST
        @Path("feed")
        @Consumes("application/atom+xml")
        @Produces("application/atom+xml")
        public Feed postFeed(String feed);

        @GET
        @Path("xmltype")
        @Produces("application/atom+xml")
        public Entry getXmlType();

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
        Assert.assertEquals("dive into mark", feed.getTitle());
        Assert.assertTrue(feed.getSubtitle().indexOf("effortless") > -1);
        Assert.assertEquals(feed.getRights(), "Copyright (c) 2003, Mark Pilgrim");
        Assert.assertEquals(feed.getGenerator().getUri(), new URI("http://www.example.com/"));
        Assert.assertEquals(feed.getGenerator().getVersion(), "1.0");
        Assert.assertEquals(feed.getGenerator().getText().trim(), "Example Toolkit");
        Assert.assertNotNull(feed.getUpdated());
        Assert.assertEquals(feed.getId().toString(), "tag:example.org,2003:3");
        Link alternate = feed.getLinkByRel("alternate");
        Assert.assertNotNull(alternate);
        Assert.assertEquals(alternate.getType(), MediaType.valueOf("text/html"));
        Assert.assertEquals(alternate.getHreflang(), "en");
        Assert.assertEquals(alternate.getHref(), new URI("http://example.org/"));
        Link self = feed.getLinkByRel("self");
        Assert.assertNotNull(self);
        Assert.assertEquals(self.getType(), MediaType.APPLICATION_ATOM_XML_TYPE);
        Assert.assertEquals(self.getHreflang(), null);
        Assert.assertEquals(self.getHref(), new URI("http://example.org/feed.atom"));

        Assert.assertEquals(1, feed.getEntries().size());
        Entry entry = feed.getEntries().get(0);
        Assert.assertEquals("Atom draft-07 snapshot", entry.getTitle());
        alternate = entry.getLinkByRel("alternate");
        Assert.assertNotNull(alternate);
        Assert.assertEquals(alternate.getType(), MediaType.valueOf("text/html"));
        Assert.assertEquals(alternate.getHref(), new URI("http://example.org/2005/04/02/atom"));
        Link enclosure = entry.getLinkByRel("enclosure");
        Assert.assertNotNull(enclosure);
        Assert.assertEquals(enclosure.getType(), MediaType.valueOf("audio/mpeg"));
        Assert.assertEquals(enclosure.getLength(), "1337");
        Assert.assertEquals(enclosure.getHref(), new URI(
                "http://example.org/audio/ph34r_my_podcast.mp3"));
        Assert.assertEquals(entry.getId(), new URI("tag:example.org,2003:3.2397"));
        Assert.assertNotNull(entry.getUpdated());
        Assert.assertNotNull(entry.getPublished());
        Person author = entry.getAuthors().get(0);
        Assert.assertEquals(author.getName(), "Mark Pilgrim");
        Assert.assertEquals(author.getUri(), new URI("http://example.org/"));
        Assert.assertEquals(author.getEmail(), "f8dy@example.com");
        Assert.assertEquals(entry.getContributors().get(0).getName(), "Sam Ruby");
        Assert.assertEquals(entry.getContributors().get(1).getName(), "Joe Gregorio");
        Assert.assertEquals(entry.getContent().getType(), MediaType.APPLICATION_XML_TYPE);
        Assert.assertEquals(entry.getContent().getLanguage(), "en");
        Assert.assertEquals(entry.getContent().getBase(), new URI("http://diveintomark.org/"));
        System.out.println(entry.getContent().getElement().getNodeName());
        System.out.println(entry.getContent().getElement().getNamespaceURI());
        Customer cust = entry.getContent().getJAXBObject(Customer.class);
        Assert.assertEquals(cust.getName(), "bill");

    }

    @Before
    public void setUp() throws Exception {
        dispatcher.getRegistry().addPerRequestResource(AtomServer.class);
    }

    @Test
    public void testAtomFeed() throws Exception {
        HttpClient client = new HttpClient();
        GetMethod get = createGetMethod("/atom/feed");
        int status = client.executeMethod(get);
        Assert.assertEquals(200, status);
        System.out.println(get.getResponseBodyAsString());

        AtomServerInterface intf = createProxy(AtomServerInterface.class);
        Feed feed = intf.postFeed(RFC_COMPLEX_XML);
        assertFeed(feed);

        // Thread.sleep(1000000);

    }

    @Test
    public void testAtomEntry() throws Exception {
        HttpClient client = new HttpClient();
        GetMethod get = createGetMethod("/atom/entry");
        int status = client.executeMethod(get);
        Assert.assertEquals(200, status);
        System.out.println(get.getResponseBodyAsString());
    }

    @Test
    public void testXmlType() throws Exception {
        AtomServerInterface intf = createProxy(AtomServerInterface.class);
        Entry entry = intf.getXmlType();
        DataCollectionRecord record = entry.getContent().getJAXBObject(DataCollectionRecord.class);
        Assert.assertEquals("hello world", record.getCollectedData());

    }

}
