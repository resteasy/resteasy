package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

@Singleton
@Path("root")
public class SingletonRootResource {
    @EJB
    SingletonSubResource sub;

    @EJB
    SingletonLocalIF rl;

    @Path("sub")
    public SingletonSubResource getSub() {
        return sub;
    }

    @Path("intfsub")
    public SingletonLocalIF getLocalSub() {
        return rl;
    }

    @Inject
    private Application injectedApplication;
    private boolean isJaxrsInjectedPriorToPostConstruct = false;

    @PostConstruct
    public void postConstruct() {
        isJaxrsInjectedPriorToPostConstruct = injectedApplication != null;
    }

    @Path("injected")
    @GET
    public String injected() {
        return String.valueOf(isJaxrsInjectedPriorToPostConstruct);
    }

    @Path("exception")
    @GET
    public String throwException() {
        throw new EJBException(new WebApplicationException(Response.Status.CREATED));
    }
}
