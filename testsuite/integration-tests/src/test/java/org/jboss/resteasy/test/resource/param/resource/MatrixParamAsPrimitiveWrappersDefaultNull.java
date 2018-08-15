package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/wrappers/default/null")
public class MatrixParamAsPrimitiveWrappersDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") Boolean v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") Byte v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") Short v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") Integer v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") Long v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") Float v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") Double v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") Character v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }
}
