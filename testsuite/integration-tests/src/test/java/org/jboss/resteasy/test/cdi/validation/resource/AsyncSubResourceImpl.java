package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@RequestScoped
public class AsyncSubResourceImpl implements AsyncSubResource {

    private static final Logger LOG = Logger.getLogger(AsyncSubResourceImpl.class);

    public AsyncSubResourceImpl() {
        LOG.info("creating AsyncSubResourceImpl");
    }

    @Override
    public void getAll(AsyncResponse asyncResponse, QueryBeanParamImpl beanParam) {
        LOG.info("sub#getAll: beanParam#getParam valid? " + beanParam.getParam());
        asyncResponse.resume(Response.ok().build());
    }
}
