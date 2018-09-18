package org.jboss.resteasy.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Link;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkHeader
{
   private Map<String, Link> linksByRelationship = new HashMap<String, Link>();
   private Map<String, Link> linksByTitle = new HashMap<String, Link>();
   private List<Link> links = new ArrayList<Link>();
   private static final HeaderDelegate<LinkHeader> delegate = RuntimeDelegate.getInstance().createHeaderDelegate(LinkHeader.class);

   public LinkHeader addLink(final Link link)
   {
      links.add(link);
      return this;
   }

   public LinkHeader addLink(final String title, final String rel, final String href, final String type)
   {
      final Link link = Link.fromUri(href).rel(rel).title(title).type(type).build();
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
    * Index of links by relationship "rel" or "rev"
    *
    * @return map
    */
   public Map<String, Link> getLinksByRelationship()
   {
      return linksByRelationship;
   }

   /**
    * Index of links by title
    *
    * @return map
    */
   public Map<String, Link> getLinksByTitle()
   {
      return linksByTitle;
   }

   /**
    * All the links defined
    *
    * @return links
    */
   public List<Link> getLinks()
   {
      return links;
   }

   public static LinkHeader valueOf(String val)
   {
      return delegate.fromString(val);
   }

   public String toString()
   {
      return delegate.toString(this);
   }
}
