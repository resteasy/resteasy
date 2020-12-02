package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("header")
public interface MultiValuedParamDefaultParamConverterHeaderResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   String headerConstructorSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/separator/set")
   @GET
   String headerConstructorSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   String headerConstructorSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/array")
   @GET
   String headerConstructorSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/regex/list")
   @GET
   String headerConstructorRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/regex/set")
   @GET
   String headerConstructorRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/sortedset")
   @GET
   String headerConstructorRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/array")
   @GET
   String headerConstructorRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/default/list")
   @GET
   String headerConstructorDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/default/set")
   @GET
   String headerConstructorDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/sortedset")
   @GET
   String headerConstructorDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/array")
   @GET
   String headerConstructorDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass

   @Path("valueOf/separator/list")
   @GET
   String headerValueOfSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/separator/set")
   @GET
   String headerValueOfSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/sortedset")
   @GET
   String headerValueOfSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/array")
   @GET
   String headerValueOfSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/regex/list")
   @GET
   String headerValueOfRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/regex/set")
   @GET
   String headerValueOfRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/sortedset")
   @GET
   String headerValueOfRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/array")
   @GET
   String headerValueOfRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/default/list")
   @GET
   String headerValueOfDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/default/set")
   @GET
   String headerValueOfDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/sortedset")
   @GET
   String headerValueOfDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/array")
   @GET
   String headerValueOfDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("fromString/separator/list")
   @GET
   String headerFromStringSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/separator/set")
   @GET
   String headerFromStringSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/sortedset")
   @GET
   String headerFromStringSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/array")
   @GET
   String headerFromStringSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/regex/list")
   @GET
   String headerFromStringRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/regex/set")
   @GET
   String headerFromStringRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/sortedset")
   @GET
   String headerFromStringRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/array")
   @GET
   String headerFromStringRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/default/list")
   @GET
   String headerFromStringDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/default/set")
   @GET
   String headerFromStringDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/sortedset")
   @GET
   String headerFromStringDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/array")
   @GET
   String headerFromStringDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("headerDelegate/separator/list")
   @GET
   String headerHeaderDelegateSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list);

   @Path("headerDelegate/separator/set")
   @GET
   String headerHeaderDelegateSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/separator/sortedset")
   @GET
   String headerHeaderDelegateSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/separator/array")
   @GET
   String headerHeaderDelegateSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array);

   @Path("headerDelegate/regex/list")
   @GET
   String headerHeaderDelegateRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list);

   @Path("headerDelegate/regex/set")
   @GET
   String headerHeaderDelegateRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/regex/sortedset")
   @GET
   String headerHeaderDelegateRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/regex/array")
   @GET
   String headerHeaderDelegateRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array);

   @Path("headerDelegate/default/list")
   @GET
   String headerHeaderDelegateDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list);

   @Path("headerDelegate/default/set")
   @GET
   String headerHeaderDelegateDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/default/sortedset")
   @GET
   String headerHeaderDelegateDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/default/array")
   @GET
   String headerHeaderDelegateDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array);

   @Path("paramConverter/separator/list")
   @GET
   String headerParamConverterSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/separator/set")
   @GET
   String headerParamConverterSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/sortedset")
   @GET
   String headerParamConverterSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/array")
   @GET
   String headerParamConverterSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/regex/list")
   @GET
   String headerParamConverterRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/regex/set")
   @GET
   String headerParamConverterRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/sortedset")
   @GET
   String headerParamConverterRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/array")
   @GET
   String headerParamConverterRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/default/list")
   @GET
   String headerParamConverterDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/default/set")
   @GET
   String headerParamConverterDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/sortedset")
   @GET
   String headerParamConverterDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/array")
   @GET
   String headerParamConverterDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("boolean")
   @GET
   String headerBoolean(@HeaderParam("h") @Separator("#") boolean[] array);

   @Path("byte")
   @GET
   String headerByte(@HeaderParam("h") @Separator("#") byte[] array);

   @Path("char")
   @GET
   String headerChar(@HeaderParam("h") @Separator("#") char[] array);

   @Path("short")
   @GET
   String headerShort(@HeaderParam("h") @Separator("#") short[] array);

   @Path("int")
   @GET
   String headerInt(@HeaderParam("h") @Separator("#") int[] array);

   @Path("long")
   @GET
   String headerLong(@HeaderParam("h") @Separator("#") long[] array);

   @Path("float")
   @GET
   String headerFloat(@HeaderParam("h") @Separator("#") float[] array);

   @Path("double")
   @GET
   String headerDouble(@HeaderParam("h") @Separator("#") double[] array);
}

