package org.jboss.resteasy.test.providers.atom;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.ClassResolver;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ModelTest
{

   private static final String XML = "<content xmlns=\"http://www.w3.org/2005/Atom\" language=\"en\">Text\n" +
           "</content>";


   @Test
   public void testContentText() throws Exception
   {
      JAXBContext ctx = JAXBContext.newInstance(Content.class);
      Content content = (Content) ctx.createUnmarshaller().unmarshal(new StringReader(XML));
      System.out.println(content.getText());
      System.out.println(content.getLanguage());

   }

   /**
    * RESTEASY-242
    *
    * @throws Exception
    */
   @Test
   public void testContentSetElement() throws Exception
   {
      Content c = new Content();
      c.setElement(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("Test"));
   }


   @Test
   public void testContent() throws Exception
   {

      Content content = new Content();
      content.setJAXBObject(new CustomerAtom("bill"));
      JAXBContext ctx = JAXBContext.newInstance(Content.class, CustomerAtom.class);

      Marshaller marshaller = ctx.createMarshaller();

      marshaller.setProperty("com.sun.xml.bind.indentString", "   ");
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      StringWriter writer = new StringWriter();

      NamespacePrefixMapper mapper = new NamespacePrefixMapper()
      {
         public String getPreferredPrefix(String namespace, String s1, boolean b)
         {
            if (namespace.equals("http://www.w3.org/2005/Atom"))
            {
               return "atom";
            }
            else return s1;
         }
      };

      ClassResolver resolver = new ClassResolver()
      {
         @Nullable
         public Class<?> resolveElementName(@NotNull String ns, @NotNull String location) throws Exception
         {
            System.out.println("Resolve: " + ns + " " + location);
            return null;
         }
      };

      //marshaller.setProperty(ClassResolver.class.getName(), resolver);

      marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
      marshaller.marshal(content, writer);
      marshaller.marshal(content, System.out);

      System.out.println("**********");
      System.out.println(writer.toString());
      content = (Content) ctx.createUnmarshaller().unmarshal(new StringReader(writer.toString()));

      //JAXBElement<Customer> cust = ctx.createUnmarshaller().unmarshal(node, Customer.class);
      //System.out.println(cust.getValue().getName());

      CustomerAtom cust = content.getJAXBObject(CustomerAtom.class);
      System.out.println(cust.getName());
   }


   @Test
   public void testIt() throws Exception
   {
      Feed feed = new Feed();
      feed.setId(new URI("http://example.com/42"));
      feed.setTitle("Yo");
      feed.setUpdated(new Date());
      Link link = new Link();
      link.setHref(new URI("http://localhost"));
      link.setRel("edit");
      feed.getLinks().add(link);
      feed.getAuthors().add(new Person("Bill Burke"));

      JAXBContext ctx = JAXBContext.newInstance(Feed.class);


      Marshaller marshaller = ctx.createMarshaller();

      NamespacePrefixMapper mapper = new NamespacePrefixMapper()
      {
         public String getPreferredPrefix(String namespace, String s1, boolean b)
         {
            if (namespace.equals("http://www.w3.org/2005/Atom")) return "atom";
            else return s1;
         }
      };

      marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
      marshaller.setProperty("com.sun.xml.bind.indentString", "   ");
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      StringWriter writer = new StringWriter();

      marshaller.marshal(feed, writer);

      feed = (Feed) ctx.createUnmarshaller().unmarshal(new StringReader(writer.toString()));

      marshaller.marshal(feed, System.out);


   }

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

   @Test
   public void testRFC() throws Exception
   {
      JAXBContext ctx = JAXBContext.newInstance(Feed.class);
      Feed feed = (Feed) ctx.createUnmarshaller().unmarshal(new StringReader(RFC_COMPLEX_XML));
      Marshaller marshaller = ctx.createMarshaller();

      NamespacePrefixMapper mapper = new NamespacePrefixMapper()
      {
         public String getPreferredPrefix(String namespace, String s1, boolean b)
         {
            if (namespace.equals("http://www.w3.org/2005/Atom")) return "atom";
            else return s1;
         }
      };

      //marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
      marshaller.setProperty("com.sun.xml.bind.indentString", "   ");
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      marshaller.marshal(feed, System.out);


   }
}
