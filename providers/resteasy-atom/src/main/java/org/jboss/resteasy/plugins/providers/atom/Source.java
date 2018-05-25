package org.jboss.resteasy.plugins.providers.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Per RFC4287:</p>
 * <pre>
 *  If an atom:entry is copied from one feed into another feed, then the
 *  source atom:feed's metadata (all child elements of atom:feed other
 *  than the atom:entry elements) MAY be preserved within the copied
 *  entry by adding an atom:source child element, if it is not already
 *  present in the entry, and including some or all of the source feed's
 *  Metadata elements as the atom:source element's children.  Such
 *  metadata SHOULD be preserved if the source atom:feed contains any of
 *  the child elements atom:author, atom:contributor, atom:rights, or
 *  atom:category and those child elements are not present in the source
 *  atom:entry.
 *
 *  atomSource =
 *     element atom:source {
 *        atomCommonAttributes,
 *        (atomAuthor*
 *         &amp; atomCategory*
 *         &amp; atomContributor*
 *         &amp; atomGenerator?
 *         &amp; atomIcon?
 *         &amp; atomId?
 *         &amp; atomLink*
 *         &amp; atomLogo?
 *         &amp; atomRights?
 *         &amp; atomSubtitle?
 *         &amp; atomTitle?
 *         &amp; atomUpdated?
 *         &amp; extensionElement*)
 *     }
 *
 *  The atom:source element is designed to allow the aggregation of
 *  entries from different feeds while retaining information about an
 *  entry's source feed.  For this reason, Atom Processors that are
 *  performing such aggregation SHOULD include at least the required
 *  feed-level Metadata elements (atom:id, atom:title, and atom:updated)
 *  in the atom:source element.
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"title", "subtitle", "categories", "updated", "id", "links", "authors", "contributors", "rights",
        "icon", "logo", "generator"})
public class Source extends CommonAttributes
{
   private List<Person> authors = new ArrayList<Person>();
   private List<Category> categories = new ArrayList<Category>();
   private List<Person> contributors = new ArrayList<Person>();
   private Generator generator;
   private URI id;
   private String title;
   private Date updated;
   private List<Link> links = new ArrayList<Link>();
   private URI icon;
   private URI logo;
   private String rights;
   private String subtitle;

   @XmlElement(name = "author")
   public List<Person> getAuthors()
   {
      return authors;
   }

   @XmlElement(name = "contributor")
   public List<Person> getContributors()
   {
      return contributors;
   }

   @XmlElement
   public URI getId()
   {
      return id;
   }

   public void setId(URI id)
   {
      this.id = id;
   }

   @XmlElement
   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   @XmlElement
   public Date getUpdated()
   {
      return updated;
   }

   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }

   public Link getLinkByRel(String name)
   {
      for (Link link : links) if (link.getRel().equals(name)) return link;
      return null;
   }

   @XmlElementRef
   public List<Link> getLinks()
   {
      return links;
   }

   @XmlElementRef
   public List<Category> getCategories()
   {
      return categories;
   }

   @XmlElementRef
   public Generator getGenerator()
   {
      return generator;
   }

   public void setGenerator(Generator generator)
   {
      this.generator = generator;
   }

   @XmlElement
   public URI getIcon()
   {
      return icon;
   }

   public void setIcon(URI icon)
   {
      this.icon = icon;
   }

   @XmlElement
   public URI getLogo()
   {
      return logo;
   }

   public void setLogo(URI logo)
   {
      this.logo = logo;
   }

   @XmlElement
   public String getRights()
   {
      return rights;
   }

   public void setRights(String rights)
   {
      this.rights = rights;
   }

   @XmlElement
   public String getSubtitle()
   {
      return subtitle;
   }

   public void setSubtitle(String subtitle)
   {
      this.subtitle = subtitle;
   }
}
