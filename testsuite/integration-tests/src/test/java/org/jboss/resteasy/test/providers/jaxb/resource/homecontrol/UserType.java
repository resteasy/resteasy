package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für UserType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="UserType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="credentials"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="roles"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="role" type="{http://creaity.de/homecontrol/rest/types/v1}RoleType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserType", propOrder = {
        "id",
        "credentials",
        "roles"
})
public class UserType {
   @XmlElement(namespace = "http://creaity.de/homecontrol/rest/types/v1")
   protected String id;
   @XmlElement(required = true)
   protected UserType.Credentials credentials;
   @XmlElementWrapper(required = true)
   @XmlElement(name = "role", namespace = "http://creaity.de/homecontrol/rest/types/v1")
   protected List<RoleType> roles = new ArrayList<RoleType>();

   /**
    * Ruft den Wert der id-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
   public String getId() {
      return id;
   }

   /**
    * Legt den Wert der id-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     {@link String }
    *
    */
   public void setId(String value) {
      this.id = value;
   }

   /**
    * Ruft den Wert der credentials-Eigenschaft ab.
    *
    * @return
    *     possible object is
    *     {@link UserType.Credentials }
    *
    */
   public UserType.Credentials getCredentials() {
      return credentials;
   }

   /**
    * Legt den Wert der credentials-Eigenschaft fest.
    *
    * @param value
    *     allowed object is
    *     {@link UserType.Credentials }
    *
    */
   public void setCredentials(UserType.Credentials value) {
      this.credentials = value;
   }

   public List<RoleType> getRoles() {
      return roles;
   }

   public void setRoles(List<RoleType> roles) {
      this.roles = roles;
   }


   /**
    * <p>Java-Klasse für anonymous complex type.
    *
    * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
    *
    * <pre>
    * &lt;complexType&gt;
    *   &lt;complexContent&gt;
    *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
    *       &lt;sequence&gt;
    *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
    *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
    *       &lt;/sequence&gt;
    *     &lt;/restriction&gt;
    *   &lt;/complexContent&gt;
    * &lt;/complexType&gt;
    * </pre>
    *
    *
    */
   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlType(name = "", propOrder = {
           "loginId",
           "password"
   })
   public static class Credentials {

      @XmlElement(required = true)
      protected String loginId;
      protected String password;

      /**
       * Ruft den Wert der loginId-Eigenschaft ab.
       *
       * @return
       *     possible object is
       *     {@link String }
       *
       */
      public String getLoginId() {
         return loginId;
      }

      /**
       * Legt den Wert der loginId-Eigenschaft fest.
       *
       * @param value
       *     allowed object is
       *     {@link String }
       *
       */
      public void setLoginId(String value) {
         this.loginId = value;
      }

      /**
       * Ruft den Wert der password-Eigenschaft ab.
       *
       * @return
       *     possible object is
       *     {@link String }
       *
       */
      public String getPassword() {
         return password;
      }

      /**
       * Legt den Wert der password-Eigenschaft fest.
       *
       * @param value
       *     allowed object is
       *     {@link String }
       *
       */
      public void setPassword(String value) {
         this.password = value;
      }

   }

}
