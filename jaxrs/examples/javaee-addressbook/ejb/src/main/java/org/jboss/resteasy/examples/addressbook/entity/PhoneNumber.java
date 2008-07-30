/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * PhoneNumber.java
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
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * A PhoneNumber.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Entity
@Table(name = "phone_number")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phoneNumber", propOrder =
{"extention", "number"})
public class PhoneNumber extends AbstractContactItem
{

   /**
   * 
   */
   private static final long serialVersionUID = -4561013202010192621L;

   @Id
   @Column(name = "phone_id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @XmlAttribute
   private Long id;

   @Column(name = "extention")
   private String extention;

   @Column(name = "number", nullable = false)
   private String number;

   /** Creates a new instance of PhoneNumber */
   public PhoneNumber()
   {
   }

   public PhoneNumber(Long phoneId)
   {
      this.id = phoneId;
   }

   public PhoneNumber(Long phoneId, String number)
   {
      this.id = phoneId;
      this.number = number;
   }

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long phoneId)
   {
      this.id = phoneId;
   }

   public String getExtention()
   {
      return this.extention;
   }

   public void setExtention(String extention)
   {
      this.extention = extention;
   }

   public String getNumber()
   {
      return this.number;
   }

   public void setNumber(String number)
   {
      this.number = number;
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
