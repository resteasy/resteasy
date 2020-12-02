package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Separator;

@Path("query")
public interface MultiValuedParamDefaultParamConverterQueryResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   String queryConstructorSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/separator/set")
   @GET
   String queryConstructorSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   String queryConstructorSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/array")
   @GET
   String queryConstructorSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/regex/list")
   @GET
   String queryConstructorRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/regex/set")
   @GET
   String queryConstructorRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/sortedset")
   @GET
   String queryConstructorRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/array")
   @GET
   String queryConstructorRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/default/list")
   @GET
   String queryConstructorDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/default/set")
   @GET
   String queryConstructorDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/sortedset")
   @GET
   String queryConstructorDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/array")
   @GET
   String queryConstructorDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass

   @Path("valueOf/separator/list")
   @GET
   String queryValueOfSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/separator/set")
   @GET
   String queryValueOfSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/sortedset")
   @GET
   String queryValueOfSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/array")
   @GET
   String queryValueOfSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/regex/list")
   @GET
   String queryValueOfRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/regex/set")
   @GET
   String queryValueOfRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/sortedset")
   @GET
   String queryValueOfRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/array")
   @GET
   String queryValueOfRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/default/list")
   @GET
   String queryValueOfDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/default/set")
   @GET
   String queryValueOfDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/sortedset")
   @GET
   String queryValueOfDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/array")
   @GET
   String queryValueOfDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("fromString/separator/list")
   @GET
   String queryFromStringSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/separator/set")
   @GET
   String queryFromStringSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/sortedset")
   @GET
   String queryFromStringSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/array")
   @GET
   String queryFromStringSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/regex/list")
   @GET
   String queryFromStringRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/regex/set")
   @GET
   String queryFromStringRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/sortedset")
   @GET
   String queryFromStringRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/array")
   @GET
   String queryFromStringRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/default/list")
   @GET
   String queryFromStringDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/default/set")
   @GET
   String queryFromStringDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/sortedset")
   @GET
   String queryFromStringDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/array")
   @GET
   String queryFromStringDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("paramConverter/separator/list")
   @GET
   String queryParamConverterSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/separator/set")
   @GET
   String queryParamConverterSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/sortedset")
   @GET
   String queryParamConverterSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/array")
   @GET
   String queryParamConverterSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/regex/list")
   @GET
   String queryParamConverterRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/regex/set")
   @GET
   String queryParamConverterRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/sortedset")
   @GET
   String queryParamConverterRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/array")
   @GET
   String queryParamConverterRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/default/list")
   @GET
   String queryParamConverterDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/default/set")
   @GET
   String queryParamConverterDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/sortedset")
   @GET
   String queryParamConverterDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/array")
   @GET
   String queryParamConverterDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("boolean")
   @GET
   String queryBoolean(@HeaderParam("h") @Separator("#") boolean[] array);

   @Path("byte")
   @GET
   String queryByte(@HeaderParam("h") @Separator("#") byte[] array);

   @Path("char")
   @GET
   String queryChar(@HeaderParam("h") @Separator("#") char[] array);

   @Path("short")
   @GET
   String queryShort(@HeaderParam("h") @Separator("#") short[] array);

   @Path("int")
   @GET
   String queryInt(@HeaderParam("h") @Separator("#") int[] array);

   @Path("long")
   @GET
   String queryLong(@HeaderParam("h") @Separator("#") long[] array);

   @Path("float")
   @GET
   String queryFloat(@HeaderParam("h") @Separator("#") float[] array);

   @Path("double")
   @GET
   String queryDouble(@HeaderParam("h") @Separator("#") double[] array);
}
