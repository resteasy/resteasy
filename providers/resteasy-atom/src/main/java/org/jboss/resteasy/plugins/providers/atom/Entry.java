package org.jboss.resteasy.plugins.providers.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Per RFC4287:</p>
 * <pre>
 * The "atom:entry" element represents an individual entry, acting as a
 * container for metadata and data associated with the entry.  This
 * element can appear as a child of the atom:feed element, or it can
 * appear as the document (i.e., top-level) element of a stand-alone
 * Atom Entry Document.
 * <p/>
 * atomEntry =
 *    element atom:entry {
 *       atomCommonAttributes,
 *       (atomAuthor*
 *        &amp; atomCategory*
 *        &amp; atomContent?
 *        &amp; atomContributor*
 *        &amp; atomId
 *        &amp; atomLink*
 *        &amp; atomPublished?
 *        &amp; atomRights?
 *        &amp; atomSource?
 *        &amp; atomSummary?
 *        &amp; atomTitle
 *        &amp; atomUpdated
 *        &amp; extensionElement*)
 *    }
 * <p/>
 * This specification assigns no significance to the order of appearance
 * of the child elements of atom:entry.
 * <p/>
 * The following child elements are defined by this specification (note
 * that it requires the presence of some of these elements):
 * <p/>
 * o  atom:entry elements MUST contain one or more atom:author elements,
 *    unless the atom:entry contains an atom:source element that
 *    contains an atom:author element or, in an Atom Feed Document, the
 *    atom:feed element contains an atom:author element itself.
 * o  atom:entry elements MAY contain any number of atom:category
 *    elements.
 * o  atom:entry elements MUST NOT contain more than one atom:content
 *    element.
 * o  atom:entry elements MAY contain any number of atom:contributor
 *    elements.
 * o  atom:entry elements MUST contain exactly one atom:id element.
 * o  atom:entry elements that contain no child atom:content element
 *    MUST contain at least one atom:link element with a rel attribute
 *    value of "alternate".
 * o  atom:entry elements MUST NOT contain more than one atom:link
 *    element with a rel attribute value of "alternate" that has the
 *    same combination of type and hreflang attribute values.
 * o  atom:entry elements MAY contain additional atom:link elements
 *    beyond those described above.
 * o  atom:entry elements MUST NOT contain more than one atom:published
 *    element.
 * o  atom:entry elements MUST NOT contain more than one atom:rights
 *    element.
 * o  atom:entry elements MUST NOT contain more than one atom:source
 *    element.
 * o  atom:entry elements MUST contain an atom:summary element in either
 *    of the following cases:
 *    *  the atom:entry contains an atom:content that has a "src"
 *       attribute (and is thus empty).
 *    *  the atom:entry contains content that is encoded in Base64;
 *       i.e., the "type" attribute of atom:content is a MIME media type
 *       [MIMEREG], but is not an XML media type [RFC3023], does not
 *       begin with "text/", and does not end with "/xml" or "+xml".
 * o  atom:entry elements MUST NOT contain more than one atom:summary
 *    element.
 * o  atom:entry elements MUST contain exactly one atom:title element.
 * o  atom:entry elements MUST contain exactly one atom:updated element.
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"title", "links", "categories", "updated", "id", "published", "authors", "contributors", "source",
        "rights", "content", "summary"})
public class Entry extends CommonAttributes
{
   @XmlElement(name = "author")
   private List<Person> authors = new ArrayList<Person>();

   @XmlElementRef
   private List<Category> categories = new ArrayList<Category>();

   @XmlElementRef
   private Content content;

   @XmlElement(name = "contributor")
   private List<Person> contributors = new ArrayList<Person>();

   @XmlElement
   private URI id;

   @XmlElementRef
   private List<Link> links = new ArrayList<Link>();

   @XmlElement
   private Date published;

   @XmlElement
   private String title;
   @XmlElement
   private Date updated;

   @XmlElement
   private String rights;

   @XmlElement
   private Source source;

   @XmlElement
   private String summary;

   public URI getId()
   {
      return id;
   }

   public void setId(URI id)
   {
      this.id = id;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

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

   public List<Link> getLinks()
   {
      return links;
   }

   public Content getContent()
   {
      return content;
   }

   public void setContent(Content content)
   {
      this.content = content;
   }

   public List<Person> getAuthors()
   {
      return authors;
   }

   public List<Category> getCategories()
   {
      return categories;
   }

   public List<Person> getContributors()
   {
      return contributors;
   }

   public Date getPublished()
   {
      return published;
   }

   public void setPublished(Date published)
   {
      this.published = published;
   }

   public String getRights()
   {
      return rights;
   }

   public void setRights(String rights)
   {
      this.rights = rights;
   }

   public Source getSource()
   {
      return source;
   }

   public void setSource(Source source)
   {
      this.source = source;
   }

   public String getSummary()
   {
      return summary;
   }

   public void setSummary(String summary)
   {
      this.summary = summary;
   }
}