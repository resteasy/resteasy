package org.jboss.resteasy.client;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.client.Link;
import org.jboss.resteasy.client.LinkHeader;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;


import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public class LinkHeaderDelegate implements RuntimeDelegate.HeaderDelegate<LinkHeader>
{
   private static class Parser
   {
      private int curr;
      private String value;
      private LinkHeader header = new LinkHeader();

      public Parser(String value)
      {
         this.value = value;
      }

      public LinkHeader getHeader()
      {
         return header;
      }

      public void parse()
      {
         String href = null;
         MultivaluedMap<String, String> attributes = new MultivaluedMapImpl<String, String>();
         while (curr < value.length())
         {

            char c = value.charAt(curr);
            if (c == '<')
            {
               if (href != null)
                  throw new IllegalArgumentException(Messages.MESSAGES.unableToParseLinkHeaderTooManyLinks(value));
               href = parseLink();
            }
            else if (c == ';' || c == ' ')
            {
               curr++;
               continue;
            }
            else if (c == ',')
            {
               populateLink(href, attributes);
               href = null;
               attributes = new MultivaluedMapImpl<String, String>();
               curr++;
            }
            else
            {
               parseAttribute(attributes);
            }
         }
         populateLink(href, attributes);


      }

      protected void populateLink(String href, MultivaluedMap<String, String> attributes)
      {
         List<String> rels = attributes.get("rel");
         List<String> revs = attributes.get("rev");
         String title = attributes.getFirst("title");
         if (title != null) attributes.remove("title");
         String type = attributes.getFirst("type");
         if (type != null) attributes.remove("type");

         Set<String> relationships = new HashSet<String>();
         if (rels != null)
         {
            relationships.addAll(rels);
            attributes.remove("rel");
         }
         if (revs != null)
         {
            relationships.addAll(revs);
            attributes.remove("rev");
         }

         for (String relationship : relationships)
         {
            StringTokenizer tokenizer = new StringTokenizer(relationship);
            while (tokenizer.hasMoreTokens())
            {
               String rel = tokenizer.nextToken();
               Link link = new Link(title, rel, href, type, attributes);
               header.getLinksByRelationship().put(rel, link);
               header.getLinksByTitle().put(title, link);
               header.getLinks().add(link);
            }

         }
      }

      public String parseLink()
      {
         int end = value.indexOf('>', curr);
         if (end == -1) throw new IllegalArgumentException(Messages.MESSAGES.unableToParseLinkHeaderNoEndToLink(value));
         String href = value.substring(curr + 1, end);
         curr = end + 1;
         return href;
      }

      public void parseAttribute(MultivaluedMap<String, String> attributes)
      {
         int end = value.indexOf('=', curr);
         if (end == -1 || end + 1 >= value.length())
            throw new IllegalArgumentException(Messages.MESSAGES.unableToParseLinkHeaderNoEndToParameter(value));
         String name = value.substring(curr, end);
         name = name.trim();
         curr = end + 1;
         String val = null;
         if (curr >= value.length())
         {
            val = "";
         }
         else
         {

            if (value.charAt(curr) == '"')
            {
               if (curr + 1 >= value.length())
                  throw new IllegalArgumentException(Messages.MESSAGES.unableToParseLinkHeaderNoEndToParameter(value));
               curr++;
               end = value.indexOf('"', curr);
               if (end == -1)
                  throw new IllegalArgumentException(Messages.MESSAGES.unableToParseLinkHeaderNoEndToParameter(value));
               val = value.substring(curr, end);
               curr = end + 1;
            }
            else
            {
               StringBuffer buf = new StringBuffer();
               while (curr < value.length())
               {
                  char c = value.charAt(curr);
                  if (c == ',' || c == ';') break;
                  buf.append(value.charAt(curr));
                  curr++;
               }
               val = buf.toString();
            }
         }
         attributes.add(name, val);

      }

   }

   public LinkHeader fromString(String value) throws IllegalArgumentException
   {
      return from(value);
   }

   public static LinkHeader from(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
      Parser parser = new Parser(value);
      parser.parse();
      return parser.getHeader();

   }

   public String toString(LinkHeader value)
   {
      if (value == null) throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
      return getString(value);
   }

   public static String getString(LinkHeader value)
   {
      StringBuffer buf = new StringBuffer();
      for (Link link : value.getLinks())
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(link.toString());
      }
      return buf.toString();
   }
}
