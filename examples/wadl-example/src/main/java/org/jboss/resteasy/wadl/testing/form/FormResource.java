package org.jboss.resteasy.wadl.testing.form;

import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/form")
public class FormResource {

    @Path("/foo")
    @POST
    public String foo(@Form FooForm foo) {
        return foo.getFoo() + foo.getBar();
    }

    @Path("/list")
    @POST
    public String list(@Form ListForm list) {
        return list.getFooForms().toString();
    }
}
