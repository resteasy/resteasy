package org.jboss.resteasy.spi;

import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstraction for LInk headers.  Also uses JAXRS classes rather than deprecated old resteasy ones.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkHeaders
{
   private Map<String, Link> linksByRelationship = new HashMap<String, Link>();
   private Map<String, Link> linksByTitle = new HashMap<String, Link>();
   private List<Link> links = new ArrayList<Link>();

   public LinkHeaders addLinks(MultivaluedMap<String, String> headers)
   {
      List<String> values = headers.get("Link");
      if (values == null) return this;
      for (String val : values)
      {
         LinkHeader linkHeader = new LinkHeaderDelegate().fromString(val);
         for (org.jboss.resteasy.spi.Link link : linkHeader.getLinks())
         {
            addLink(link.toJaxrsLink());
         }
      }
      return this;
   }

   public LinkHeaders addLinkObjects(MultivaluedMap<String, Object> headers, HeaderValueProcessor factory)
   {
      List<Object> values = headers.get("Link");
      if (values == null) return this;
      for (Object val : values)
      {
         if (val instanceof Link) addLink((Link)val);
         else
         {
            String str = factory.toHeaderString(val);
            addLink(Link.valueOf(str));
         }
      }
      return this;
   }

   public LinkHeaders addLink(final Link link)
   {
      links.add(link);
      for (String rel : link.getRels())
      {
         linksByRelationship.put(rel, link);
      }
      if (link.getTitle() != null) linksByTitle.put(link.getTitle(), link);
      return this;
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
    * @return map
    */
   public Map<String, Link> getLinksByRelationship()
   {
      return linksByRelationship;
   }

   /**
    * Index of links by title.
    *
    * @return map
    */
   public Map<String, Link> getLinksByTitle()
   {
      return linksByTitle;
   }

   /**
    * All the links defined.
    *
    * @return links
    */
   public List<Link> getLinks()
   {
      return links;
   }

}
