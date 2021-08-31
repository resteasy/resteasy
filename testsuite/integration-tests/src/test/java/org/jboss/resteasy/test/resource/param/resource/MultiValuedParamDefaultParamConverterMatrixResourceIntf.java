package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("matrix")
public interface MultiValuedParamDefaultParamConverterMatrixResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   String matrixConstructorSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/separator/set")
   @GET
   String matrixConstructorSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   String matrixConstructorSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/array")
   @GET
   String matrixConstructorSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/regex/list")
   @GET
   String matrixConstructorRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/regex/set")
   @GET
   String matrixConstructorRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/sortedset")
   @GET
   String matrixConstructorRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/array")
   @GET
   String matrixConstructorRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/default/list")
   @GET
   String matrixConstructorDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/default/set")
   @GET
   String matrixConstructorDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/sortedset")
   @GET
   String matrixConstructorDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/array")
   @GET
   String matrixConstructorDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass

   @Path("valueOf/separator/list")
   @GET
   String matrixValueOfSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/separator/set")
   @GET
   String matrixValueOfSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/sortedset")
   @GET
   String matrixValueOfSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/array")
   @GET
   String matrixValueOfSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/regex/list")
   @GET
   String matrixValueOfRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/regex/set")
   @GET
   String matrixValueOfRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/sortedset")
   @GET
   String matrixValueOfRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/array")
   @GET
   String matrixValueOfRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/default/list")
   @GET
   String matrixValueOfDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/default/set")
   @GET
   String matrixValueOfDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/sortedset")
   @GET
   String matrixValueOfDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/array")
   @GET
   String matrixValueOfDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("fromString/separator/list")
   @GET
   String matrixFromStringSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/separator/set")
   @GET
   String matrixFromStringSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/sortedset")
   @GET
   String matrixFromStringSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/array")
   @GET
   String matrixFromStringSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/regex/list")
   @GET
   String matrixFromStringRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/regex/set")
   @GET
   String matrixFromStringRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/sortedset")
   @GET
   String matrixFromStringRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/array")
   @GET
   String matrixFromStringRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/default/list")
   @GET
   String matrixFromStringDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/default/set")
   @GET
   String matrixFromStringDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/sortedset")
   @GET
   String matrixFromStringDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/array")
   @GET
   String matrixFromStringDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("paramConverter/separator/list")
   @GET
   String matrixParamConverterSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/separator/set")
   @GET
   String matrixParamConverterSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/sortedset")
   @GET
   String matrixParamConverterSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/array")
   @GET
   String matrixParamConverterSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/regex/list")
   @GET
   String matrixParamConverterRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/regex/set")
   @GET
   String matrixParamConverterRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/sortedset")
   @GET
   String matrixParamConverterRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/array")
   @GET
   String matrixParamConverterRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/default/list")
   @GET
   String matrixParamConverterDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/default/set")
   @GET
   String matrixParamConverterDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/sortedset")
   @GET
   String matrixParamConverterDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/array")
   @GET
   String matrixParamConverterDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("boolean")
   @GET
   String matrixBoolean(@HeaderParam("h") @Separator("#") boolean[] array);

   @Path("byte")
   @GET
   String matrixByte(@HeaderParam("h") @Separator("#") byte[] array);

   @Path("char")
   @GET
   String matrixChar(@HeaderParam("h") @Separator("#") char[] array);

   @Path("short")
   @GET
   String matrixShort(@HeaderParam("h") @Separator("#") short[] array);

   @Path("int")
   @GET
   String matrixInt(@HeaderParam("h") @Separator("#") int[] array);

   @Path("long")
   @GET
   String matrixLong(@HeaderParam("h") @Separator("#") long[] array);

   @Path("float")
   @GET
   String matrixFloat(@HeaderParam("h") @Separator("#") float[] array);

   @Path("double")
   @GET
   String matrixDouble(@HeaderParam("h") @Separator("#") double[] array);
}

