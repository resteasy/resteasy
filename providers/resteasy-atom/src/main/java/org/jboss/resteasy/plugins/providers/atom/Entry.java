package org.jboss.resteasy.plugins.providers.atom;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.w3c.dom.Element;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
 *
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
 *
 * This specification assigns no significance to the order of appearance
 * of the child elements of atom:entry.
 *
 * The following child elements are defined by this specification (note
 * that it requires the presence of some of these elements):
 *
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
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"titleElement", "links", "categories", "updated", "id", "published", "authors", "contributors", "source",
        "rightsElement", "content", "summaryElement", "anyOther"})
public class Entry extends CommonAttributes
{
   private List<Person> authors = new ArrayList<Person>();

   private List<Category> categories = new ArrayList<Category>();

   private Content content;

   private List<Person> contributors = new ArrayList<Person>();

   private URI id;

   private List<Link> links = new ArrayList<Link>();

   private Date published;

   private Text title;
   private Date updated;

   private Text rights;

   private Source source;

   private Text summary;
   
   private Element anyOtherElement;
   
   private List<Object> anyOther;
   
   private Object anyOtherJaxbObject;

   protected JAXBContextFinder finder;

   protected void setFinder(JAXBContextFinder finder)
   {
      this.finder = finder;
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

   @XmlElement(name = "title")
   public Text getTitleElement() 
   {
     return title;     
   }
   
   public void setTitleElement(Text title) {
     this.title = title;
   }
   
   @XmlTransient
   public String getTitle()
   {
     if (this.title == null) 
     {
       return null;
     }
      return title.getText();
   }

   public void setTitle(String title)
   {
      if (this.title == null) 
      {
        this.title = new Text();
      }
      this.title.setText(title);
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
   public Content getContent()
   {
      return content;
   }

   public void setContent(Content content)
   {
      this.content = content;
   }

   @XmlElement(name = "author")
   public List<Person> getAuthors()
   {
      return authors;
   }

   @XmlElementRef
   public List<Category> getCategories()
   {
      return categories;
   }

   @XmlElement(name = "contributor")
   public List<Person> getContributors()
   {
      return contributors;
   }

   @XmlElement
   public Date getPublished()
   {
      return published;
   }

   public void setPublished(Date published)
   {
      this.published = published;
   }

   @XmlElement(name = "rights")
   public Text getRightsElement()
   {
      return rights;
   }

   public void setRightsElement(Text rights)
   {
      this.rights = rights;
   }
   
   @XmlTransient
   public String getRights() {
     if (rights == null) 
     {
       return null;
     }
     return rights.getText();
   }

   public void setRights(String rights) 
   {
     if (this.rights == null) 
     {
       this.rights = new Text();
     }
     this.rights.setText(rights);
   }
   
   @XmlElement
   public Source getSource()
   {
      return source;
   }

   public void setSource(Source source)
   {
      this.source = source;
   }

   @XmlElement(name = "summary")
   public Text getSummaryElement()
   {
      return summary;
   }

   public void setSummaryElement(Text summary)
   {
      this.summary = summary;
   }

   @XmlTransient
   public String getSummary() {
     if (rights == null) 
     {
       return null;
     }
     return rights.getText();
   }

   public void setSummary(String summary) 
   {
     if (this.summary == null) 
     {
       this.summary = new Text();
     }
     this.summary.setText(summary);
   }
   
   
   /**
    * Get content as an XML Element if the content is XML.  Otherwise, this will just return null.
    *
    * @return {@link Element}
    */
   @XmlTransient
   public Element getAnyOtherElement()
   {
      if (anyOther == null) return null;
      if (anyOtherElement != null) return anyOtherElement;
      for (Object obj : anyOther)
      {
         if (obj instanceof Element)
         {
             anyOtherElement = (Element) obj;
            return anyOtherElement;
         }
      }
      return null;
   }
   
   @XmlMixed
   @XmlAnyElement(lax = true)
   public List<Object> getAnyOther() {
       if (anyOther == null) {
           anyOther = new ArrayList<Object>();
       }
       return this.anyOther;
   }
   /**
    * Extract the content as the provided JAXB annotated type.
    * <p>
    * This method will use a cached JAXBContext used by the Resteasy JAXB providers
    * or, if those are not existent, it will create a new JAXBContext from scratch
    * using the class.
    *
    * @param <T> type
    * @param clazz                class type you are expecting
    * @param otherPossibleClasses Other classe you want to create the JAXBContext with
    * @return null if there is no XML content
    * @throws JAXBException jaxb exception
    */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T getAnyOtherJAXBObject(Class<T> clazz, Class... otherPossibleClasses) throws JAXBException {
        JAXBContext ctx = null;
        Class[] classes = {clazz};
        if (otherPossibleClasses != null && otherPossibleClasses.length > 0) {
            classes = new Class[1 + otherPossibleClasses.length];
            classes[0] = clazz;
            for (int i = 0; i < otherPossibleClasses.length; i++) classes[i + 1] = otherPossibleClasses[i];
        }
        if (finder != null) {
            ctx = finder.findCacheContext(MediaType.APPLICATION_XML_TYPE, null, classes);
        } else {
            ctx = JAXBContext.newInstance(classes);
        }
 
        Object obj = null;
 
        if (getAnyOtherElement() != null) {
            obj = ctx.createUnmarshaller().unmarshal(getAnyOtherElement());
        } else {
            if (getAnyOther().size() == 0) return null;
            for (Object _obj : getAnyOther()) {
                for (Class _clazz : classes) {
                    if (_obj.getClass().equals(_clazz)) {
                        obj = _obj;
                        break;
                    }
                }
            }
 
            if (obj == null)
                return null;
        }
 
        if (obj instanceof JAXBElement) {
            anyOtherJaxbObject = ((JAXBElement) obj).getValue();
            return (T) anyOtherJaxbObject;
        } else {
            anyOtherJaxbObject = obj;
            return (T) obj;
        }
	}

   
   /**
    * Returns previous extracted jaxbobject from a call to getJAXBObject(Class{@literal <}T{@literal >} clazz)
    * or value passed in through a previous setJAXBObject().
    *
    * @return object
    */
   @XmlTransient
   public Object getAnyOtherJAXBObject()
   {
      return anyOtherJaxbObject;
   }

   public void setAnyOtherJAXBObject(Object obj)
   {
      if (anyOther == null) anyOther = new ArrayList<Object>();
      if (anyOtherJaxbObject != null && anyOther != null) anyOther.clear();
      if (!obj.getClass().isAnnotationPresent(XmlRootElement.class) && obj.getClass().isAnnotationPresent(XmlType.class))
      {
          anyOther.add(JAXBXmlTypeProvider.wrapInJAXBElement(obj, obj.getClass()));
      }
      else
      {
          anyOther.add(obj);
      }
      anyOtherJaxbObject = obj;
   }
}
