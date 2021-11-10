package org.jboss.resteasy.plugins.providers.atom.app;

import org.w3c.dom.Element;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Per RFC5023:
 * </p>
 *
 * <pre>
 * The "app:collection" element describes a Collection.  The app:
 *    collection element MUST contain one atom:title element.
 *
 *    The app:collection element MAY contain any number of app:accept
 *    elements, indicating the types of representations accepted by the
 *    Collection.  The order of such elements is not significant.
 *
 *    The app:collection element MAY contain any number of app:categories
 *    elements.
 *
 *    appCollection =
 *       element app:collection {
 *          appCommonAttributes,
 *          attribute href { atomURI  },
 *          ( atomTitle
 *            {@literal &} appAccept*
 *            {@literal &} appCategories*
 *            {@literal &} extensionSansTitleElement* )
 *       }
 * </pre>
 *
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "collection", propOrder = { "href", "title", "accept",
      "categories", "any" })
public class AppCollection extends AppCommonAttributes {

   private static final long serialVersionUID = 3466473348035916400L;
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected String href;
   @XmlElement(namespace = "http://www.w3.org/2005/Atom", required = true)
   protected String title;
   protected List<AppAccept> accept;
   protected List<AppCategories> categories;
   @XmlAnyElement(lax = true)
   protected List<Object> any;


   public AppCollection() {}

   public AppCollection(final String href, final String title) {
      super();
      this.href = href;
      this.title = title;
   }

   /**
    * Gets the value of the title property.
    *
    * @return possible object is {@link String }
    *
    */
   public String getTitle() {
      return title;
   }

   /**
    * Sets the value of the title property.
    *
    * @param value
    *            allowed object is {@link String }
    *
    */
   public void setTitle(String value) {
      this.title = value;
   }

   /**
    * Gets the value of the accept property.
    *
    * <p>
    * This accessor method returns a reference to the live list, not a
    * snapshot. Therefore any modification you make to the returned list will
    * be present inside the JAXB object. This is why there is not a
    * <CODE>set</CODE> method for the accept property.
    *
    * <p>
    * For example, to add a new item, do as follows:
    *
    * <pre>
    * getAccept().add(newItem);
    * </pre>
    *
    *
    * <p>
    * Objects of the following type(s) are allowed in the list {@link String }
    *
    *  @return list of {@link AppAccept}
    */
   public List<AppAccept> getAccept() {
      if (accept == null) {
         accept = new ArrayList<AppAccept>();
      }
      return this.accept;
   }

   /**
    * Gets the value of the categories property.
    *
    * <p>
    * This accessor method returns a reference to the live list, not a
    * snapshot. Therefore any modification you make to the returned list will
    * be present inside the JAXB object. This is why there is not a
    * <CODE>set</CODE> method for the categories property.
    *
    * <p>
    * For example, to add a new item, do as follows:
    *
    * <pre>
    * getCategories().add(newItem);
    * </pre>
    *
    *
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link AppCategories }
    *
    * @return list of {@link AppCategories}
    *
    */
   public List<AppCategories> getCategories() {
      if (categories == null) {
         categories = new ArrayList<AppCategories>();
      }
      return this.categories;
   }

   /**
    * Gets the value of the any property.
    *
    * <p>
    * This accessor method returns a reference to the live list, not a
    * snapshot. Therefore any modification you make to the returned list will
    * be present inside the JAXB object. This is why there is not a
    * <CODE>set</CODE> method for the any property.
    *
    * <p>
    * For example, to add a new item, do as follows:
    *
    * <pre>
    * getAny().add(newItem);
    * </pre>
    *
    *
    * <p>
    * Objects of the following type(s) are allowed in the list {@link Element }
    * {@link Object }
    *
    * @return list of objects
    *
    */
   public List<Object> getAny() {
      if (any == null) {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * Gets the value of the href property.
    *
    * @return possible object is {@link String }
    *
    */
   public String getHref() {
      return href;
   }

   /**
    * Sets the value of the href property.
    *
    * @param value
    *            allowed object is {@link String }
    *
    */
   public void setHref(String value) {
      this.href = value;
   }

}
