package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

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
