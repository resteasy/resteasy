package org.jboss.resteasy.test.providers.jettison.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

public interface CRUDEntityWebservice {

    @POST
    @Path("/")
    UserEntity create(UserEntity entity);
}
