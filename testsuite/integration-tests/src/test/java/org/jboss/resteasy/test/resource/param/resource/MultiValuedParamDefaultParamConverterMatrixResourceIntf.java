package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("matrix")
public interface MultiValuedParamDefaultParamConverterMatrixResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String matrixConstructorSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set")
   @GET
   public String matrixConstructorSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   public String matrixConstructorSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/separator/array")
   @GET
   public String matrixConstructorSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);
 
   @Path("constructor/regex/list")
   @GET
   public String matrixConstructorRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String matrixConstructorRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset")
   @GET
   public String matrixConstructorRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/array")
   @GET
   public String matrixConstructorRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   @Path("constructor/default/list")
   @GET
   public String matrixConstructorDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String matrixConstructorDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String matrixConstructorDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/array")
   @GET
   public String matrixConstructorDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String matrixValueOfSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String matrixValueOfSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String matrixValueOfSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/array")
   @GET
   public String matrixValueOfSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/regex/list")
   @GET
   public String matrixValueOfRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String matrixValueOfRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String matrixValueOfRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/array")
   @GET
   public String matrixValueOfRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/default/list")
   @GET
   public String matrixValueOfDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set")
   @GET
   public String matrixValueOfDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String matrixValueOfDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/array")
   @GET
   public String matrixValueOfDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("fromString/separator/list")
   @GET
   public String matrixFromStringSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String matrixFromStringSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String matrixFromStringSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/array")
   @GET
   public String matrixFromStringSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/regex/list")
   @GET
   public String matrixFromStringRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String matrixFromStringRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String matrixFromStringRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/array")
   @GET
   public String matrixFromStringRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/default/list")
   @GET
   public String matrixFromStringDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String matrixFromStringDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String matrixFromStringDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/array")
   @GET
   public String matrixFromStringDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("paramConverter/separator/list")
   @GET
   public String matrixParamConverterSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String matrixParamConverterSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String matrixParamConverterSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/array")
   @GET
   public String matrixParamConverterSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/regex/list")
   @GET
   public String matrixParamConverterRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String matrixParamConverterRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String matrixParamConverterRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/array")
   @GET
   public String matrixParamConverterRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/default/list")
   @GET
   public String matrixParamConverterDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String matrixParamConverterDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String matrixParamConverterDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/array")
   @GET
   public String matrixParamConverterDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("boolean")
   @GET
   public String matrixBoolean(@HeaderParam("h") @Separator("#") boolean[] array);
   
   @Path("byte")
   @GET
   public String matrixByte(@HeaderParam("h") @Separator("#") byte[] array);
   
   @Path("char")
   @GET
   public String matrixChar(@HeaderParam("h") @Separator("#") char[] array);
   
   @Path("short")
   @GET
   public String matrixShort(@HeaderParam("h") @Separator("#") short[] array);
   
   @Path("int")
   @GET
   public String matrixInt(@HeaderParam("h") @Separator("#") int[] array);
   
   @Path("long")
   @GET
   public String matrixLong(@HeaderParam("h") @Separator("#") long[] array);
   
   @Path("float")
   @GET
   public String matrixFloat(@HeaderParam("h") @Separator("#") float[] array);
 
   @Path("double")
   @GET
   public String matrixDouble(@HeaderParam("h") @Separator("#") double[] array);
}

