package org.jboss.resteasy.test.providers.namespace.mapping;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NamespaceMappingTestBase complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="NamespaceMappingTestBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamespaceMappingTestBase", propOrder = {
      "id",
      "name",
      "desc"
})
@XmlSeeAlso({
      NamespaceMappingTestExtends.class
})
public class NamespaceMappingTestBase {

   @XmlElement(required = true)
   protected String id;
   @XmlElement(required = true)
   protected String name;
   @XmlElement(required = true)
   protected String desc;

   /**
    * Gets the value of the id property.
    *
    * @return possible object is
    * {@link String }
    */
   public String getId() {
      return id;
   }

   /**
    * Sets the value of the id property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setId(String value) {
      this.id = value;
   }

   /**
    * Gets the value of the name property.
    *
    * @return possible object is
    * {@link String }
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the value of the name property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setName(String value) {
      this.name = value;
   }

   /**
    * Gets the value of the desc property.
    *
    * @return possible object is
    * {@link String }
    */
   public String getDesc() {
      return desc;
   }

   /**
    * Sets the value of the desc property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setDesc(String value) {
      this.desc = value;
   }

}
