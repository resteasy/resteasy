package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.UriInfo;

@Stateless(name = "SingletonTestBean")
@Local({ SingletonLocalIF.class })
public class SingletonTestBean implements SingletonLocalIF {

    public SingletonTestBean() {
    }

    public void remove() {
    }

    @Inject
    private UriInfo ui;

    @Override
    @GET
    public String get() {
        return "GET: " + ui.getRequestUri().toASCIIString() +
                " Hello From Singleton Local EJB Sub";
    }
}
