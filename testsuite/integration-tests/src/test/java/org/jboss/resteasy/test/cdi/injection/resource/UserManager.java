package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class UserManager {

   @Inject
   private UserRepository userRepository;

   /**
    * Return all supported languages
    *
    * @return
    */
   public ApplicationUser getUser() {
      ApplicationUser user = userRepository.find(1L);
      return user;
   }

}
