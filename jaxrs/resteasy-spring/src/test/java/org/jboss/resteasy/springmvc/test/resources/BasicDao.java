package org.jboss.resteasy.springmvc.test.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Repository;

@Repository
public class BasicDao {

	@Context UriInfo uri;
	
	public String getPath(){
		return uri.getPath();
	}
}
