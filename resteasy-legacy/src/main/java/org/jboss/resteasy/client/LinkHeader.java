package org.jboss.resteasy.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.client.LinkHeaderDelegate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public class LinkHeader
{
   private Map<String, Link> linksByRelationship = new HashMap<String, Link>();
   private Map<String, Link> linksByTitle = new HashMap<String, Link>();
   private List<Link> links = new ArrayList<Link>();

   public LinkHeader addLink(final Link link)
   {
      links.add(link);
      return this;
   }

   public LinkHeader addLink(final String title, final String rel, final String href, final String type)
   {
      final Link link = new Link(title, rel, href, type, null);
      return addLink(link);
   }

   public Link getLinkByTitle(String title)
   {
      return linksByTitle.get(title);
   }

   public Link getLinkByRelationship(String rel)
   {
      return linksByRelationship.get(rel);
   }

   /**
    * Index of links by relationship "rel" or "rev".
    *
    * @return map of links
    */
   public Map<String, Link> getLinksByRelationship()
   {
      return linksByRelationship;
   }

   /**
    * Index of links by title.
    *
    * @return map of links
    */
   public Map<String, Link> getLinksByTitle()
   {
      return linksByTitle;
   }

   /**
    * All the links defined.
    *
    * @return list of links
    */
   public List<Link> getLinks()
   {
      return links;
   }
   
   public static LinkHeader valueOf(String val)
   {
      return LinkHeaderDelegate.from(val);
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer();
      for (Link link : getLinks())
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(link.toString());
      }
      return buf.toString();
   }
}
