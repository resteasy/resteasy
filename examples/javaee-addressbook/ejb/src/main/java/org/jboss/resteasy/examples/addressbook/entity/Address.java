/*
 * Address.java
 *
 * Created on October 2, 2006, 8:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jboss.resteasy.examples.addressbook.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 */

@Entity
@Table(name = "address")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address", propOrder =
{"addressLine1", "addressLine2", "addressLine3", "city", "state", "zip"})
public class Address extends AbstractContactItem
{

   /**
    * 
    */
   private static final long serialVersionUID = 562257818622225184L;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "address_id", nullable = false, unique = true)
   @XmlAttribute
   private Long id;

   @Column(name = "address_line_1")
   private String addressLine1;

   @Column(name = "address_line_2")
   private String addressLine2;

   @Column(name = "address_line_3")
   private String addressLine3;

   @Column(name = "city", nullable = false)
   private String city;

   @Column(name = "state", nullable = false)
   private String state;

   @Column(name = "zip", nullable = false)
   private String zip;

   /** Creates a new instance of Address */
   public Address()
   {
   }

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long addressId)
   {
      this.id = addressId;
   }

   public String getAddressLine1()
   {
      return this.addressLine1;
   }

   public void setAddressLine1(String addressLine01)
   {
      this.addressLine1 = addressLine01;
   }

   public String getAddressLine2()
   {
      return this.addressLine2;
   }

   public void setAddressLine2(String addressLine02)
   {
      this.addressLine2 = addressLine02;
   }

   public String getAddressLine3()
   {
      return this.addressLine3;
   }

   public void setAddressLine3(String addressLine03)
   {
      this.addressLine3 = addressLine03;
   }

   public String getCity()
   {
      return this.city;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   public String getState()
   {
      return this.state;
   }

   public void setState(String state)
   {
      this.state = state;
   }

   public String getZip()
   {
      return this.zip;
   }

   public void setZip(String zip)
   {
      this.zip = zip;
   }

   /**
    * JAXB Callback method used to reassociate the item with the owning contact.
    * JAXB doesn't seem to read this method from a super class and it must 
    * therefore be placed on any subclass.
    * 
    * @param unmarshaller the JAXB {@link Unmarshaller}.
    * @param parent the owning {@link Contact} instance.
    */
   public void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
   {
      super.afterUnmarshal(unmarshaller, parent);
   }
}
