package org.jboss.resteasy.validation;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 18, 2013
 */
@Path("")
public class TestResourceWithGetterViolation
{  
	private static int count = 0;
	
   @GET
   @Path("get")
   @Size(min=2, max=4)
   public String getS()
   {
   	/*
   	 * Called twice by Hibernate Validator 5 during field, property, class
   	 * constraint validation.
   	 */
   	System.out.println("getS(): count: " + count);
   	String s = count++ <= 1 ? "abc" : "a";
   	System.out.println("s: " + s);
   	return s;
   }
}
