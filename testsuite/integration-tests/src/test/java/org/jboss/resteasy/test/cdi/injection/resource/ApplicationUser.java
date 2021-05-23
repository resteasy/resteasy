package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "application_user")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationUser implements Serializable {

   /**
    * The ID property for the entity
    */
   @Id
   private Long id;

   @Column(name = "user_type")
   @Enumerated(EnumType.STRING)
   private UserType userType;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public UserType getUserType() {
      return userType;
   }

   public void setUserType(UserType userType) {
      this.userType = userType;
   }

}
