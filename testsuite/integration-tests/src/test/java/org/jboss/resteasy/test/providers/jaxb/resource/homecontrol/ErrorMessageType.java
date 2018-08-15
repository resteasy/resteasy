package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für ErrorMessageType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ErrorMessageType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="type" type="{http://creaity.de/homecontrol/rest/types/v1}ErrorType"/&gt;
 *         &lt;element name="domain" type="{http://creaity.de/homecontrol/rest/types/v1}ErrorDomainType"/&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorMessageType", propOrder = {
        "type",
        "domain",
        "message",
        "timestamp"
})
public class ErrorMessageType {

   @XmlElement(required = true)
   @XmlSchemaType(name = "string")
   protected ErrorType type;
   @XmlElement(required = true)
   @XmlSchemaType(name = "string")
   protected ErrorDomainType domain;
   @XmlElement(required = true)
   protected String message;
   @XmlElement(required = true)
   @XmlSchemaType(name = "dateTime")
   protected XMLGregorianCalendar timestamp;

   /**
    * Ruft den Wert der type-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     {@link ErrorType }
    *
    */
   public ErrorType getType() {
      return type;
   }

   /**
    * Legt den Wert der type-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     {@link ErrorType }
    *
    */
   public void setType(ErrorType value) {
      this.type = value;
   }

   /**
    * Ruft den Wert der domain-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     {@link ErrorDomainType }
    *
    */
   public ErrorDomainType getDomain() {
      return domain;
   }

   /**
    * Legt den Wert der domain-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     {@link ErrorDomainType }
    *
    */
   public void setDomain(ErrorDomainType value) {
      this.domain = value;
   }

   /**
    * Ruft den Wert der message-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getMessage() {
      return message;
   }

   /**
    * Legt den Wert der message-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setMessage(String value) {
      this.message = value;
   }

   /**
    * Ruft den Wert der timestamp-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     {@link XMLGregorianCalendar }
    *
    */
   public XMLGregorianCalendar getTimestamp() {
      return timestamp;
   }

   /**
    * Legt den Wert der timestamp-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     {@link XMLGregorianCalendar }
    *
    */
   public void setTimestamp(XMLGregorianCalendar value) {
      this.timestamp = value;
   }

}