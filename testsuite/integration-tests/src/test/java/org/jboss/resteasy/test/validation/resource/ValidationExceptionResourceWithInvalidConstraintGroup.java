package org.jboss.resteasy.test.validation.resource;

import javax.validation.GroupDefinitionException;
import javax.validation.GroupSequence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

@Path("/")
@GroupSequence({ValidationExceptionTestGroup1.class, ValidationExceptionTestGroup2.class})
public class ValidationExceptionResourceWithInvalidConstraintGroup {
	
	@Provider
	public static class GroupDefinitionExceptionMapper extends ValidationExceptionMapper<GroupDefinitionException> {
	}
	
    private String s;

    @GET
    public String test() {
        return s;
    }
}
