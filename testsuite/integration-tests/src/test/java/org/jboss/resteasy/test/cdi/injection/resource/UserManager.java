package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
