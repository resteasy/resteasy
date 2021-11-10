package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.annotations.Separator;

@Path("path")
public interface MultiValuedParamDefaultParamConverterPathResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultProviderConstructorClass

   @Path("constructor/separator/list/{p}")
   @GET
   String pathConstructorSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/separator/set/{p}")
   @GET
   String pathConstructorSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset/{p}")
   @GET
   String pathConstructorSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/array/{p}")
   @GET
   String pathConstructorSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/regex/list/{p}")
   @GET
   String pathConstructorRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/regex/set/{p}")
   @GET
   String pathConstructorRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/sortedset/{p}")
   @GET
   String pathConstructorRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/array/{p}")
   @GET
   String pathConstructorRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/default/list/{p}")
   @GET
   String pathConstructorDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/default/set/{p}")
   @GET
   String pathConstructorDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/sortedset/{p}")
   @GET
   String pathConstructorDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/array/{p}")
   @GET
   String pathConstructorDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);

   //////////////////////////////////
   // MultiValuedParamDefaultProviderValueOfClass

   @Path("valueOf/separator/list/{p}")
   @GET
   String pathValueOfSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/separator/set/{p}")
   @GET
   String pathValueOfSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/sortedset/{p}")
   @GET
   String pathValueOfSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/array/{p}")
   @GET
   String pathValueOfSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/regex/list/{p}")
   @GET
   String pathValueOfRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/regex/set/{p}")
   @GET
   String pathValueOfRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/sortedset/{p}")
   @GET
   String pathValueOfRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/array/{p}")
   @GET
   String pathValueOfRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/default/list/{p}")
   @GET
   String pathValueOfDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/default/set/{p}")
   @GET
   String pathValueOfDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/sortedset/{p}")
   @GET
   String pathValueOfDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/array/{p}")
   @GET
   String pathValueOfDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("fromString/separator/list/{p}")
   @GET
   String pathFromStringSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/separator/set/{p}")
   @GET
   String pathFromStringSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/sortedset/{p}")
   @GET
   String pathFromStringSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/array/{p}")
   @GET
   String pathFromStringSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/regex/list/{p}")
   @GET
   String pathFromStringRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/regex/set/{p}")
   @GET
   String pathFromStringRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/sortedset/{p}")
   @GET
   String pathFromStringRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/array/{p}")
   @GET
   String pathFromStringRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/default/list/{p}")
   @GET
   String pathFromStringDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/default/set/{p}")
   @GET
   String pathFromStringDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/sortedset/{p}")
   @GET
   String pathFromStringDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/array/{p}")
   @GET
   String pathFromStringDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("paramConverter/separator/list/{p}")
   @GET
   String pathParamConverterSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/separator/set/{p}")
   @GET
   String pathParamConverterSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/sortedset/{p}")
   @GET
   String pathParamConverterSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/array/{p}")
   @GET
   String pathParamConverterSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/regex/list/{p}")
   @GET
   String pathParamConverterRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/regex/set/{p}")
   @GET
   String pathParamConverterRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/sortedset/{p}")
   @GET
   String pathParamConverterRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/array/{p}")
   @GET
   String pathParamConverterRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/default/list/{p}")
   @GET
   String pathParamConverterDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/default/set/{p}")
   @GET
   String pathParamConverterDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/sortedset/{p}")
   @GET
   String pathParamConverterDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/array/{p}")
   @GET
   String pathParamConverterDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("boolean")
   @GET
   String pathBoolean(@HeaderParam("h") @Separator("#") boolean[] array);

   @Path("byte")
   @GET
   String pathByte(@HeaderParam("h") @Separator("#") byte[] array);

   @Path("char")
   @GET
   String pathChar(@HeaderParam("h") @Separator("#") char[] array);

   @Path("short")
   @GET
   String pathShort(@HeaderParam("h") @Separator("#") short[] array);

   @Path("int")
   @GET
   String pathInt(@HeaderParam("h") @Separator("#") int[] array);

   @Path("long")
   @GET
   String pathLong(@HeaderParam("h") @Separator("#") long[] array);

   @Path("float")
   @GET
   String pathFloat(@HeaderParam("h") @Separator("#") float[] array);

   @Path("double")
   @GET
   String pathDouble(@HeaderParam("h") @Separator("#") double[] array);
}

