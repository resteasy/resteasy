package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/array/default/null")
public class MatrixParamAsPrimitiveArrayDefaultNull {
   @GET
   @Produces("application/boolean")
   public String doGetBoolean(@MatrixParam("boolean") boolean[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }

   @GET
   @Produces("application/byte")
   public String doGetByte(@MatrixParam("byte") byte[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }

   @GET
   @Produces("application/short")
   public String doGetShort(@MatrixParam("short") short[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }

   @GET
   @Produces("application/int")
   public String doGetInteger(@MatrixParam("int") int[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }

   @GET
   @Produces("application/long")
   public String doGetLong(@MatrixParam("long") long[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }

   @GET
   @Produces("application/float")
   public String doGetFloat(@MatrixParam("float") float[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }

   @GET
   @Produces("application/double")
   public String doGetDouble(@MatrixParam("double") double[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }

   @GET
   @Produces("application/char")
   public String doGetCharacter(@MatrixParam("char")  char[] v) {
      Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, v.length == 0);
      return "content";
   }
}
