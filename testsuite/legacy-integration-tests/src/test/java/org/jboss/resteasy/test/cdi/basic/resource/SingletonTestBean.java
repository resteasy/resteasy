package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Stateless(name = "SingletonTestBean")
@Local({SingletonLocalIF.class})
public class SingletonTestBean implements SingletonLocalIF {

    public SingletonTestBean() {
    }

    public void remove() {
    }

    @Context
    private UriInfo ui;

    @Override
    @GET
    public String get() {
        return "GET: " + ui.getRequestUri().toASCIIString() +
                " Hello From Singleton Local EJB Sub";
    }
}