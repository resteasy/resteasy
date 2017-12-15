package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;


import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ErrorDomainType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorDomainType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SERVER"/&gt;
 *     &lt;enumeration value="REQUEST"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ErrorDomainType")
@XmlEnum
public enum ErrorDomainType {


   /**
    * The error happend on the server side and can't be corrected with changing the request
    *                         format
    *
    *
    */
   SERVER,

   /**
    * The error happend because of the request or the requests content.
    *
    */
   REQUEST;

   public String value() {
      return name();
   }

   public static ErrorDomainType fromValue(String v) {
      return valueOf(v);
   }

}
