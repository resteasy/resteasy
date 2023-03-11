package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/list/default/null")
public class MatrixParamAsPrimitiveListDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@MatrixParam("boolean") List<Boolean> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.size() == 0);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@MatrixParam("byte") List<Byte> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.size() == 0);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@MatrixParam("short") List<Short> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.size() == 0);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@MatrixParam("int") List<Integer> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.size() == 0);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@MatrixParam("long") List<Long> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.size() == 0);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@MatrixParam("float") List<Float> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.size() == 0);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@MatrixParam("double") List<Double> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.size() == 0);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@MatrixParam("char") List<Character> v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.isEmpty());
        return "content";
    }
}
