package org.jboss.resteasy.test.cdi.validation.resource;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

@RequestScoped
public class SubResourceImpl implements SubResource {
    static boolean methodEntered;
    private static final Logger LOG = Logger.getLogger(SubResourceImpl.class);

    @Override
    public Response getAll(QueryBeanParamImpl beanParam) {
        LOG.info("beanParam#getParam valid? " + beanParam.getParam());
        methodEntered = true;
        return Response.ok().build();
    }
}
