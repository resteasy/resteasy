package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.POST;

public class LocatorWithClassHierarchyMiddleResource extends LocatorWithClassHierarchyPathParamResource {

    private final String returnValue;

    public LocatorWithClassHierarchyMiddleResource() {
        returnValue = null;
    }

    protected LocatorWithClassHierarchyMiddleResource(final String id1, final String id2) {
        if ("ParamEntityWithConstructor".equals(id1)) {
            returnValue = paramEntityWithConstructorTest(new LocatorWithClassHierarchyParamEntityWithConstructor(id2));
        } else {
            returnValue = two(id1, new LocatorWithClassHierarchyPathSegmentImpl(id2));
        }
    }

    @POST
    public String returnValue() {
        return returnValue;
    }

}
