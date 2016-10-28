package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.junit.Assert;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

@Path("/form/{id}")
@Produces("application/x-www-form-urlencoded")
@Consumes("application/x-www-form-urlencoded")
public class FormResource {

    @POST
    public MultivaluedMap<String, String> postObject(@BeanParam FormResourceValueHolder value) {
        MultivaluedMap<String, String> rtn = new MultivaluedMapImpl<String, String>();
        rtn.add("booleanValue", value.getBooleanValue().toString());
        rtn.add("doubleValue", value.getDoubleValue().toString());
        rtn.add("integerValue", value.getIntegerValue().toString());
        rtn.add("longValue", value.getLongValue().toString());
        rtn.add("shortValue", value.getShortValue().toString());
        rtn.add("name", value.getName());

        Assert.assertEquals(value.getHeaderParam(), 42);
        Assert.assertEquals(value.getQueryParam(), 42);
        Assert.assertEquals(value.getId(), 42);
        Assert.assertEquals(value.getDefaultValue(), 42);
        return rtn;
    }
}
