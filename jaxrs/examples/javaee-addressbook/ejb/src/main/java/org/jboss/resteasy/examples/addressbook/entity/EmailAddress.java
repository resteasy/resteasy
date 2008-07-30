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
 * EmailAddress.java
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * A EmailAddress.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Entity
@Table(name = "email_address")
@NamedQueries({
   @NamedQuery(name = "EmailAddress.findByContactId",
               query = "from EmailAddress where contact.id = :contactId")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "emailAddress")
public class EmailAddress extends AbstractContactItem
{

   /**
   * 
   */
   private static final long serialVersionUID = 1608260634194054046L;

   @Id
   @Column(name = "email_id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @XmlAttribute
   private Long id;

   @Column(name = "email_address", nullable = false)
   private String emailAddress;
   
   /** Creates a new instance of EmailAddress */
   public EmailAddress()
   {
   }

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getEmailAddress()
   {
      return this.emailAddress;
   }

   public void setEmailAddress(String emailAddress)
   {
      this.emailAddress = emailAddress;
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
