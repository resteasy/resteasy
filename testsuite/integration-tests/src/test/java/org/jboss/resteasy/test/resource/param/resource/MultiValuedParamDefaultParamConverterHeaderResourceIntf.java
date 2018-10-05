package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("header")
public interface MultiValuedParamDefaultParamConverterHeaderResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String headerConstructorSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set")
   @GET
   public String headerConstructorSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   public String headerConstructorSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/separator/array")
   @GET
   public String headerConstructorSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   @Path("constructor/regex/list")
   @GET
   public String headerConstructorRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String headerConstructorRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset")
   @GET
   public String headerConstructorRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/array")
   @GET
   public String headerConstructorRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   @Path("constructor/default/list")
   @GET
   public String headerConstructorDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String headerConstructorDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String headerConstructorDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/array")
   @GET
   public String headerConstructorDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String headerValueOfSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String headerValueOfSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String headerValueOfSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/array")
   @GET
   public String headerValueOfSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/regex/list")
   @GET
   public String headerValueOfRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String headerValueOfRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String headerValueOfRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/array")
   @GET
   public String headerValueOfRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/default/list")
   @GET
   public String headerValueOfDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set")
   @GET
   public String headerValueOfDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String headerValueOfDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/array")
   @GET
   public String headerValueOfDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("fromString/separator/list")
   @GET
   public String headerFromStringSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String headerFromStringSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String headerFromStringSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/array")
   @GET
   public String headerFromStringSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/regex/list")
   @GET
   public String headerFromStringRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String headerFromStringRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String headerFromStringRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/array")
   @GET
   public String headerFromStringRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/default/list")
   @GET
   public String headerFromStringDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String headerFromStringDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String headerFromStringDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/array")
   @GET
   public String headerFromStringDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("headerDelegate/separator/list")
   @GET
   public String headerHeaderDelegateSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list);

   @Path("headerDelegate/separator/set")
   @GET
   public String headerHeaderDelegateSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/separator/sortedset")
   @GET
   public String headerHeaderDelegateSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/separator/array")
   @GET
   public String headerHeaderDelegateSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array);

   @Path("headerDelegate/regex/list")
   @GET
   public String headerHeaderDelegateRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list);

   @Path("headerDelegate/regex/set")
   @GET
   public String headerHeaderDelegateRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/regex/sortedset")
   @GET
   public String headerHeaderDelegateRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/regex/array")
   @GET
   public String headerHeaderDelegateRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array);

   @Path("headerDelegate/default/list")
   @GET
   public String headerHeaderDelegateDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list);

   @Path("headerDelegate/default/set")
   @GET
   public String headerHeaderDelegateDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/default/sortedset")
   @GET
   public String headerHeaderDelegateDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set);

   @Path("headerDelegate/default/array")
   @GET
   public String headerHeaderDelegateDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array);

   @Path("paramConverter/separator/list")
   @GET
   public String headerParamConverterSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String headerParamConverterSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String headerParamConverterSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/array")
   @GET
   public String headerParamConverterSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/regex/list")
   @GET
   public String headerParamConverterRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String headerParamConverterRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String headerParamConverterRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/array")
   @GET
   public String headerParamConverterRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
 
   @Path("paramConverter/default/list")
   @GET
   public String headerParamConverterDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String headerParamConverterDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String headerParamConverterDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/array")
   @GET
   public String headerParamConverterDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("boolean")
   @GET
   public String headerBoolean(@HeaderParam("h") @Separator("#") boolean[] array);
   
   @Path("byte")
   @GET
   public String headerByte(@HeaderParam("h") @Separator("#") byte[] array);
   
   @Path("char")
   @GET
   public String headerChar(@HeaderParam("h") @Separator("#") char[] array);
   
   @Path("short")
   @GET
   public String headerShort(@HeaderParam("h") @Separator("#") short[] array);
   
   @Path("int")
   @GET
   public String headerInt(@HeaderParam("h") @Separator("#") int[] array);
   
   @Path("long")
   @GET
   public String headerLong(@HeaderParam("h") @Separator("#") long[] array);
   
   @Path("float")
   @GET
   public String headerFloat(@HeaderParam("h") @Separator("#") float[] array);
 
   @Path("double")
   @GET
   public String headerDouble(@HeaderParam("h") @Separator("#") double[] array);
}

