package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * User: rsearls
 * Date: 2/17/17
 */
@Path("/capture")
public class ResourceLocatorRegexCapturingGroup {

    @Path("basic")
    public ResourceLocatorRegexCapturingGroupSubResourceNoPath basic() {
        return new ResourceLocatorRegexCapturingGroupSubResourceNoPath ("basic");
    }

    @Path("BASIC")
    public ResourceLocatorRegexCapturingGroupSubResourceWithPath basicTwo() {
        return new ResourceLocatorRegexCapturingGroupSubResourceWithPath("BASIC");
    }

    @Path("{name: (nobird|NOBIRD)}")
    public ResourceLocatorRegexCapturingGroupSubResourceNoPath nobird(@PathParam("name") String name) {
        return new ResourceLocatorRegexCapturingGroupSubResourceNoPath(name);
    }

    @Path("{name: (bird|BIRD)}")
    public ResourceLocatorRegexCapturingGroupSubResourceWithPath bird(@PathParam("name") String name) {
        return new ResourceLocatorRegexCapturingGroupSubResourceWithPath(name);
    }

    @Path("{name: a/(fly|FLY)/b}")
    public ResourceLocatorRegexCapturingGroupSubResourceWithPath fly(@PathParam("name") String name) {
        return new ResourceLocatorRegexCapturingGroupSubResourceWithPath(name);
    }

    @Path("{name: a/(nofly|NOFLY)/b}")
    public ResourceLocatorRegexCapturingGroupSubResourceNoPath nofly(@PathParam("name") String name) {
        return new ResourceLocatorRegexCapturingGroupSubResourceNoPath(name);
    }
}
