package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ErrorType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NOT_FOUND"/&gt;
 *     &lt;enumeration value="VALIDATION"/&gt;
 *     &lt;enumeration value="INTERNAL_SERVER_ERROR"/&gt;
 *     &lt;enumeration value="SECURITY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ErrorType")
@XmlEnum
public enum ErrorType {


   /**
    * The requested resource was not found
    *
    */
   NOT_FOUND,

   /**
    * The requested format did not match the specification
    *
    */
   VALIDATION,

   /**
    * An error happend during the request processing
    *
    */
   INTERNAL_SERVER_ERROR,

   /**
    * Request can't be processed, because the caller has insufficient authorization
    *
    *
    */
   SECURITY;

   public String value() {
      return name();
   }

   public static ErrorType fromValue(String v) {
      return valueOf(v);
   }

}

