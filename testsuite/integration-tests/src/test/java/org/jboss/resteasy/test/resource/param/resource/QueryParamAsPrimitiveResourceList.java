package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/list")
public class QueryParamAsPrimitiveResourceList implements QueryParamAsPrimitiveResourceResourceListInterface {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") List<Boolean> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v.get(0).booleanValue());
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v.get(1).booleanValue());
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v.get(2).booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") List<Byte> v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v.get(0).byteValue());
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v.get(1).byteValue());
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v.get(2).byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") List<Short> v) {
        Assert.assertTrue((short) 32767 == v.get(0).shortValue());
        Assert.assertTrue((short) 32767 == v.get(1).shortValue());
        Assert.assertTrue((short) 32767 == v.get(2).shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") List<Integer> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v.get(0).intValue());
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v.get(1).intValue());
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v.get(2).intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") List<Long> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v.get(0).longValue());
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v.get(1).longValue());
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v.get(2).longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") List<Float> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v.get(0).floatValue(), 0.0f);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v.get(1).floatValue(), 0.0f);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v.get(2).floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") List<Double> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v.get(0).doubleValue(), 0.0);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v.get(1).doubleValue(), 0.0);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v.get(2).doubleValue(), 0.0);
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char")List<Character> v) {
    	  Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v.get(0).charValue());
          Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v.get(1).charValue());
          Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v.get(2).charValue());
          return "content";
    }
}
