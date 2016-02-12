/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.jboss.resteasy.resteasy1296;

import javax.ejb.Stateless;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Gunnar Morling
 */
@Path("/hikes")
@Stateless
public class HikeResource {

	@NotNull
	private String name;

	public String getName() {
		return name;
	}

	@POST
	@Path("/createHike")
	@Consumes("application/json")
	@Produces({"application/json", "text/plain"})
	public void createHike(@Min(1) long id, String from, String to) {
		// nothing to do
	}
}
