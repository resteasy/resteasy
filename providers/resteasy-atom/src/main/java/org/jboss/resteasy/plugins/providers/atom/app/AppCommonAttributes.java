package org.jboss.resteasy.plugins.providers.atom.app;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Attributes common across all atom app types
 *
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlTransient
public class AppCommonAttributes implements Serializable
{
   private static final long serialVersionUID = -6132753912772236190L;
   @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
   @XmlSchemaType(name = "anySimpleType")
   private String base;
   @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
   private String lang;
   @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   private String space;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the base property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   @jakarta.xml.bind.annotation.XmlTransient
   public String getBase() {
      return base;
   }

   /**
    * Sets the value of the base property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setBase(String value) {
      this.base = value;
   }

   /**
    * Gets the value of the lang property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   @jakarta.xml.bind.annotation.XmlTransient
   public String getLang() {
      return lang;
   }

   /**
    * Sets the value of the lang property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setLang(String value) {
      this.lang = value;
   }

   /**
    * Gets the value of the space property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   @jakarta.xml.bind.annotation.XmlTransient
   public String getSpace() {
      return space;
   }

   /**
    * Sets the value of the space property.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setSpace(String value) {
      this.space = value;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    *
    * <p>
    * the map is keyed by the name of the attribute and
    * the value is the string value of the attribute.
    *
    * the map returned by this method is live, and you can add new attribute
    * by updating the map directly. Because of this design, there's no setter.
    *
    *
    * @return
    *     always non-null
    */
   public Map<QName, String> getOtherAttributes() {
      return otherAttributes;
   }

}
