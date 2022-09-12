package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für IDType complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="IDType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IDType", propOrder = {
      "id"
})
public class IDType {

   @XmlElement(required = true)
   protected String id;

   /**
    * Ruft den Wert der id-Eigenschaft ab.
    *
    * @return possible object is
    * {@link String }
    */
   public String getId() {
      return id;
   }

   /**
    * Legt den Wert der id-Eigenschaft fest.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setId(String value) {
      this.id = value;
   }

}
