package org.jboss.resteasy.resteasy1125;

import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * RESTEASY-1125
 *
 * Nov 19, 2014
 */
@Stateless
@Path("/test")
public class TestResource extends OtherAbstractResource<Model> {

}