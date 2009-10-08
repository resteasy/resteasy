package org.jboss.resteasy.plugins.delegates;

import org.jboss.resteasy.spi.LinkHeader;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
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
               if (href != null) throw new IllegalArgumentException("Uanble to parse Link header.  Too many links in declaration: " + value);
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
         if (end == -1) throw new IllegalArgumentException("Unable to parse Link header.  No end to link: " + value);
         String href = value.substring(curr + 1, end);
         curr = end + 1;
         return href;
      }

      public void parseAttribute(MultivaluedMap<String, String> attributes)
      {
         int end = value.indexOf('=', curr);
         if (end == -1 || end + 1 >= value.length()) throw new IllegalArgumentException("Unable to parse Link header.  No end to parameter: " + value);
         String name = value.substring(curr, end);
         name = name.trim();
         curr = end + 1;
         String val = null;
         end = value.indexOf(';', curr);
         if (end == -1)
         {
            val = value.substring(curr);
            curr = value.length();
         }
         else
         {
            val = value.substring(curr, end);
            curr = end;
         }
         val = val.trim();
         if (val.length() > 0 && val.charAt(0) == '"')
         {
            end = val.indexOf('"', 1);
            if (end == -1) throw new IllegalArgumentException("Unable to parse Link header.  No end to quote: " + value);
            val = val.substring(1, end);
         }
         attributes.add(name, val);

      }

   }
   public LinkHeader fromString(String value) throws IllegalArgumentException
   {
      Parser parser = new Parser(value);
      parser.parse();
      return parser.getHeader();

   }

   public String toString(LinkHeader value)
   {
      StringBuffer buf = new StringBuffer();
      for (Link link : value.getLinks())
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append("<").append(link.getHref()).append(">");
         buf.append("; rel=\"").append(link.getRelationship()).append("\"");
         if (link.getType() != null) buf.append("; type=").append(link.getType());
         if (link.getTitle() != null) buf.append("; title=\"").append(link.getTitle()).append("\"");
         for (String key : link.getExtensions().keySet())
         {
            List<String> values = link.getExtensions().get("key");
            for (String val : values)
            {
               buf.append("; ").append(key).append("=\"").append(val).append("\"");
            }
         }
      }
      return buf.toString();
   }
}
