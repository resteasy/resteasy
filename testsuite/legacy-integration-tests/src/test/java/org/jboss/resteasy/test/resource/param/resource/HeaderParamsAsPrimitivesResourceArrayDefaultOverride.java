package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/array/default/override")
public class HeaderParamsAsPrimitivesResourceArrayDefaultOverride
        implements
        HeaderParamsAsPrimitivesArrayDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") boolean[] v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v[0]);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@HeaderParam("int") @DefaultValue("0") short[] v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v[0]);
        return "content";
    }
}
