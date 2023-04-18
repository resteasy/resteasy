package org.jboss.resteasy.test.cdi.validation.resource;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

public abstract class AbstractAsyncRootResource implements AsyncRootResource {

    private static final Logger LOG = Logger.getLogger(AbstractAsyncRootResource.class);

    @Override
    public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam) {
        LOG.info("abstract async#getAll: beanParam#getParam valid? " + beanParam.getParam());
        asyncResponse.resume(Response.ok().build());
    }
}
