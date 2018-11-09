package org.jboss.resteasy.test.spring.deployment.resource;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Contact {
   private String firstName, lastName;

   public Contact() {
   }

   public Contact(final String firstName, final String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
   }

   public String getFirstName() {
      return firstName;
   }

   @FormParam("firstName")
   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   @FormParam("lastName")
   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public boolean equals(Object other) {
      // normal checks apply here...
      Contact otherContact = (Contact) other;
      return otherContact.firstName.equals(this.firstName)
            && otherContact.lastName.equals(this.lastName);
   }

   public int hashCode() {
      return firstName.hashCode() ^ lastName.hashCode();
   }
}
