package org.jboss.resteasy.test.nextgen.wadl;


/**
 * Created by weli on 6/14/16.
 */
public abstract class WADLTestSetup {


    protected org.jboss.resteasy.wadl.jaxb.Method findMethodById(org.jboss.resteasy.wadl.jaxb.Resource resource, String id) {
        for (Object methodOrResource : resource.getMethodOrResource()) {
            if (methodOrResource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Method.class))
                if (((org.jboss.resteasy.wadl.jaxb.Method) methodOrResource).getId().equals(id))
                    return (org.jboss.resteasy.wadl.jaxb.Method) methodOrResource;
        }
        return null;
    }

    protected org.jboss.resteasy.wadl.jaxb.Resource findResourceByName(Object target, String resourceName) {
        if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Application.class)) {
            for (org.jboss.resteasy.wadl.jaxb.Resource resource : ((org.jboss.resteasy.wadl.jaxb.Application) target).getResources().get(0).getResource()) {
                if (resource.getPath().equals(resourceName)) {
                    return resource;
                }
            }
        } else if (target.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class)) {
            for (Object resource : ((org.jboss.resteasy.wadl.jaxb.Resource) target).getMethodOrResource()) {
                if (resource.getClass().equals(org.jboss.resteasy.wadl.jaxb.Resource.class) && ((org.jboss.resteasy.wadl.jaxb.Resource) resource).getPath().equals(resourceName)) {
                    return (org.jboss.resteasy.wadl.jaxb.Resource) resource;
                }
            }
        }
        return null;
    }
}
