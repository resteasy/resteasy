package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für base64Binary complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="base64Binary"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;base64Binary"&gt;
 *       &lt;attribute ref="{http://www.w3.org/2005/05/xmlmime}contentType"/&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "base64Binary", propOrder = {
        "value"
})
@XmlSeeAlso({
        BinaryType.class
})
public class Base64Binary {

   @XmlValue
   protected byte[] value;
   @XmlAttribute(name = "contentType", namespace = "http://www.w3.org/2005/05/xmlmime")
   protected String contentType;

   /**
    * Ruft den Wert der value-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     byte[]
    */
   public byte[] getValue() {
      return value;
   }

   /**
    * Legt den Wert der value-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     byte[]
    */
   public void setValue(byte[] value) {
      this.value = value;
   }

   /**
    * Ruft den Wert der contentType-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getContentType() {
      return contentType;
   }

   /**
    * Legt den Wert der contentType-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setContentType(String value) {
      this.contentType = value;
   }

}
