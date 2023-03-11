package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.junit.Assert;

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
