package org.jboss.resteasy.resteasy1125;

import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


/**
 * RESTEASY-1125
 *
 * Nov 19, 2014
 */
@Path("test2")
@Stateless
public class TestResource2 extends AbstractResource<Model>{

}
