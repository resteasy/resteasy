package org.jboss.resteasy.test.providers;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.jboss.resteasy.plugins.providers.atom.Text;
import org.jboss.resteasy.test.providers.resource.AtomProviderModelCustomerAtom;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for atom provider model
 * @tpSince RESTEasy 3.0.16
 */
public class AtomProviderModelTest {

    protected final Logger logger = LogManager.getLogger(AtomProviderModelTest.class.getName());

    private static final String XML = "<content xmlns=\"http://www.w3.org/2005/Atom\" language=\"en\">Text\n" +
            "</content>";

    private static final String RFC_COMPLEX_XML =
            "   <feed xmlns=\"http://www.w3.org/2005/Atom\">\n" +
                    "     <title type=\"text\">dive into mark</title>\n" +
                    "     <subtitle type=\"html\">\n" +
                    "       A &lt;em&gt;lot&lt;/em&gt; of effort\n" +
                    "       went into making this effortless\n" +
                    "     </subtitle>\n" +
                    "     <updated>2005-07-31T12:29:29Z</updated>\n" +
                    "     <id>tag:example.org,2003:3</id>\n" +
                    "     <link rel=\"alternate\" type=\"text/html\"\n" +
                    "      hreflang=\"en\" href=\"http://example.org/\"/>\n" +
                    "     <link rel=\"self\" type=\"application/atom+xml\"\n" +
                    "      href=\"http://example.org/feed.atom\"/>\n" +
                    "     <rights>Copyright (c) 2003, Mark Pilgrim</rights>\n" +
                    "     <generator uri=\"http://www.example.com/\" version=\"1.0\">\n" +
                    "       Example Toolkit\n" +
                    "     </generator>\n" +
                    "     <entry>\n" +
                    "       <title>Atom draft-07 snapshot</title>\n" +
                    "       <link rel=\"alternate\" type=\"text/html\"\n" +
                    "        href=\"http://example.org/2005/04/02/atom\"/>\n" +
                    "       <link rel=\"enclosure\" type=\"audio/mpeg\" length=\"1337\"\n" +
                    "        href=\"http://example.org/audio/ph34r_my_podcast.mp3\"/>\n" +
                    "       <id>tag:example.org,2003:3.2397</id>\n" +
                    "       <updated>2005-07-31T12:29:29Z</updated>\n" +
                    "       <published>2003-12-13T08:29:29-04:00</published>\n" +
                    "       <author>\n" +
                    "         <name>Mark Pilgrim</name>\n" +
                    "         <uri>http://example.org/</uri>\n" +
                    "         <email>f8dy@example.com</email>\n" +
                    "       </author>\n" +
                    "       <contributor>\n" +
                    "         <name>Sam Ruby</name>\n" +
                    "       </contributor>\n" +
                    "       <contributor>\n" +
                    "         <name>Joe Gregorio</name>\n" +
                    "       </contributor>\n" +
                    "       <content type=\"xhtml\" xml:lang=\"en\"\n" +
                    "        xml:base=\"http://diveintomark.org/\">\n" +
                    "         <div xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                    "           <p><i>[Update: The Atom draft is finished.]</i></p>\n" +
                    "         </div>\n" +
                    "       </content>\n" +
                    "     </entry>\n" +
                    "   </feed>";

    /**
     * @tpTestDetails Test JAXB content - text form
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContentText() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Content.class);
        Content content = (Content) ctx.createUnmarshaller().unmarshal(new StringReader(XML));
        logger.info(content.getText());
        logger.info(content.getLanguage());

    }

    /**
     * @tpTestDetails Regression test for RESTEASY-242
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContentSetElement() throws Exception {
        Content c = new Content();
        c.setElement(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("Test"));
    }


    /**
     * @tpTestDetails Check JAXB content
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContent() throws Exception {

        Content content = new Content();
        content.setJAXBObject(new AtomProviderModelCustomerAtom("bill"));
        JAXBContext ctx = JAXBContext.newInstance(Content.class, AtomProviderModelCustomerAtom.class);

        Marshaller marshaller = ctx.createMarshaller();

        marshaller.setProperty("com.sun.xml.bind.indentString", "   ");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();

        NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
            public String getPreferredPrefix(String namespace, String s1, boolean b) {
                if (namespace.equals("http://www.w3.org/2005/Atom")) {
                    return "atom";
                } else {
                    return s1;
                }
            }
        };

        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
        marshaller.marshal(content, writer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        marshaller.marshal(content, ps);
        if (logger.isDebugEnabled()) {
	        logger.debug("Result: " + ps.toString());
	
	        logger.debug("**********");
	        logger.debug(writer.toString());
        }
        content = (Content) ctx.createUnmarshaller().unmarshal(new StringReader(writer.toString()));

        AtomProviderModelCustomerAtom cust = content.getJAXBObject(AtomProviderModelCustomerAtom.class);
        logger.info(cust.getName());
    }


    /**
     * @tpTestDetails Check Atom Provider Binding
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBinding() throws Exception {
        Feed feed = new Feed();
        feed.setId(new URI("http://example.com/42"));
        feed.setTitle("Yo");
        feed.setUpdated(new Date());
        Link link = new Link();
        link.setHref(new URI("http://localhost"));
        link.setRel("edit");
        feed.getLinks().add(link);
        feed.getAuthors().add(new Person("Bill Burke"));

        Entry entry = new Entry();
        Text summary = new Text("<h1>Ho ho ho</h1>", "html");
        entry.setSummaryElement(summary);

        Text rights = new Text("(c) no rights");
        entry.setRightsElement(rights);

        entry.setTitle("<p>This is the <i>title</i></p>");
        entry.getTitleElement().setType(MediaType.APPLICATION_XHTML_XML_TYPE);

        feed.getEntries().add(entry);

        JAXBContext ctx = JAXBContext.newInstance(Feed.class);


        Marshaller marshaller = ctx.createMarshaller();

        NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
            public String getPreferredPrefix(String namespace, String s1, boolean b) {
                if (namespace.equals("http://www.w3.org/2005/Atom")) {
                    return "atom";
                } else {
                    return s1;
                }
            }
        };

        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
        marshaller.setProperty("com.sun.xml.bind.indentString", "   ");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter writer = new StringWriter();

        marshaller.marshal(feed, writer);

        feed = (Feed) ctx.createUnmarshaller().unmarshal(new StringReader(writer.toString()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        marshaller.marshal(feed, ps);
        logger.info("Result: " + ps.toString());
    }


    /**
     * @tpTestDetails Check RFC
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRFC() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Feed.class);
        Feed feed = (Feed) ctx.createUnmarshaller().unmarshal(new StringReader(RFC_COMPLEX_XML));
        Marshaller marshaller = ctx.createMarshaller();

        NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
            public String getPreferredPrefix(String namespace, String s1, boolean b) {
                if (namespace.equals("http://www.w3.org/2005/Atom")) {
                    return "atom";
                } else {
                    return s1;
                }
            }
        };

        //marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
        marshaller.setProperty("com.sun.xml.bind.indentString", "   ");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        marshaller.marshal(feed, ps);
        logger.info("Result: " + ps.toString());
    }
}
