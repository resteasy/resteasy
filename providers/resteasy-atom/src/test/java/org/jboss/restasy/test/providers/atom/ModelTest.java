package org.jboss.restasy.test.providers.atom;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.ClassResolver;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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


   @Test
   public void testContent() throws Exception
   {

      Content content = new Content();
      content.setJAXBObject(new Customer("bill"));
      JAXBContext ctx = JAXBContext.newInstance(Content.class, Customer.class);

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

      Customer cust = content.getJAXBObject(Customer.class);
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
      ResteasyProviderFactory.initializeInstance();
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

   /*
   @Test
   public void testBench() throws Exception
   {
      ResteasyProviderFactory.initializeInstance();
      JAXBContext ctx = JAXBContext.newInstance(Feed.class);
      HashMap map = new HashMap();

      {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 500; i++)
      {
         String foo = Feed.class.getName() + Entry.class.getName();
         map.get(foo);
         marshal(ctx);
      }
      System.out.println("Time took cached: " + (System.currentTimeMillis() - start));
      }


      {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 500; i++)
      {
         ctx = JAXBContext.newInstance(Feed.class);
         marshal(ctx);
      }
      System.out.println("Time took uncached: " + (System.currentTimeMillis() - start));
      }


   }
   */

   private void marshal(JAXBContext ctx)
           throws JAXBException
   {
      Feed feed = (Feed) ctx.createUnmarshaller().unmarshal(new StringReader(RFC_COMPLEX_XML));
      Marshaller marshaller = ctx.createMarshaller();
      StringWriter writer = new StringWriter();
      marshaller.marshal(feed, writer);
   }
}
