package org.jboss.resteasy.test.core.servlet.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.annotations.FollowUpRequired;
import org.junit.Assert;

@Provider
@ApplicationScoped
@FollowUpRequired("The @ApplicationScope annotation can be removed once @Provider is a bean defining annotation.")
public class ServletConfigExceptionMapper implements ExceptionMapper<ServletConfigException> {
    private ServletConfigApplication application;
    private ServletConfig servletConfig;
    private ServletContext context;

    @FollowUpRequired("This can be removed once RESTEasy no longer attempts to create resources.")
    public ServletConfigExceptionMapper() {

    }

    @Inject
    public ServletConfigExceptionMapper(final Application application,
            final ServletConfig servletConfig, final ServletContext context) {
        this.application = (ServletConfigApplication) application;
        this.servletConfig = servletConfig;
        this.context = context;
        Assert.assertEquals("hello", this.application.getHello());
        Assert.assertEquals("servlet hello", this.servletConfig.getInitParameter("servlet.greeting"));
        Assert.assertEquals("context hello", this.context.getInitParameter("context.greeting"));
    }

    public Response toResponse(ServletConfigException exception) {
        return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED).build();
    }
}
