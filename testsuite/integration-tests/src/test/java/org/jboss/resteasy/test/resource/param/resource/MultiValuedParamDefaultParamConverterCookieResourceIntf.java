package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("cookie")
public interface MultiValuedParamDefaultParamConverterCookieResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   String cookieConstructorSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterConstructorClass> list);;

   @Path("constructor/separator/set")
   @GET
   String cookieConstructorSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);;

   @Path("constructor/separator/sortedset")
   @GET
   String cookieConstructorSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/array")
   @GET
   String cookieConstructorSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterConstructorClass[] array);;

   @Path("constructor/regex/list")
   @GET
   String cookieConstructorRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/regex/set")
   @GET
   String cookieConstructorRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/sortedset")
   @GET
   String cookieConstructorRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/regex/array")
   @GET
   String cookieConstructorRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterConstructorClass[] array);

   @Path("constructor/default/list")
   @GET
   String cookieConstructorDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);

   @Path("constructor/default/set")
   @GET
   String cookieConstructorDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/sortedset")
   @GET
   String cookieConstructorDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/default/array")
   @GET
   String cookieConstructorDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass

   @Path("valueOf/separator/list")
   @GET
   String cookieValueOfSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/separator/set")
   @GET
   String cookieValueOfSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/sortedset")
   @GET
   String cookieValueOfSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/separator/array")
   @GET
   String cookieValueOfSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/regex/list")
   @GET
   String cookieValueOfRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/regex/set")
   @GET
   String cookieValueOfRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/sortedset")
   @GET
   String cookieValueOfRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/regex/array")
   @GET
   String cookieValueOfRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("valueOf/default/list")
   @GET
   String cookieValueOfDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);

   @Path("valueOf/default/set")
   @GET
   String cookieValueOfDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/sortedset")
   @GET
   String cookieValueOfDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);

   @Path("valueOf/default/array")
   @GET
   String cookieValueOfDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);

   @Path("fromString/separator/list")
   @GET
   String cookieFromStringSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/separator/set")
   @GET
   String cookieFromStringSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/sortedset")
   @GET
   String cookieFromStringSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/separator/array")
   @GET
   String cookieFromStringSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/regex/list")
   @GET
   String cookieFromStringRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/regex/set")
   @GET
   String cookieFromStringRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/sortedset")
   @GET
   String cookieFromStringRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/regex/array")
   @GET
   String cookieFromStringRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("fromString/default/list")
   @GET
   String cookieFromStringDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);

   @Path("fromString/default/set")
   @GET
   String cookieFromStringDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/sortedset")
   @GET
   String cookieFromStringDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);

   @Path("fromString/default/array")
   @GET
   String cookieFromStringDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);

   @Path("paramConverter/separator/list")
   @GET
   String cookieParamConverterSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/separator/set")
   @GET
   String cookieParamConverterSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/sortedset")
   @GET
   String cookieParamConverterSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/separator/array")
   @GET
   String cookieParamConverterSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/regex/list")
   @GET
   String cookieParamConverterRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/regex/set")
   @GET
   String cookieParamConverterRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/sortedset")
   @GET
   String cookieParamConverterRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/regex/array")
   @GET
   String cookieParamConverterRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("paramConverter/default/list")
   @GET
   String cookieParamConverterDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);

   @Path("paramConverter/default/set")
   @GET
   String cookieParamConverterDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/sortedset")
   @GET
   String cookieParamConverterDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/array")
   @GET
   String cookieParamConverterDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);

   @Path("boolean")
   @GET
   String cookieBoolean(@CookieParam("c") @Separator("#") boolean[] array);

   @Path("byte")
   @GET
   String cookieByte(@CookieParam("c") @Separator("#") byte[] array);

   @Path("char")
   @GET
   String cookieChar(@CookieParam("c") @Separator("#") char[] array);

   @Path("short")
   @GET
   String cookieShort(@CookieParam("c") @Separator("#") short[] array);

   @Path("int")
   @GET
   String cookieInt(@CookieParam("c") @Separator("#") int[] array);

   @Path("long")
   @GET
   String cookieLong(@CookieParam("c") @Separator("#") long[] array);

   @Path("float")
   @GET
   String cookieFloat(@CookieParam("c") @Separator("#") float[] array);

   @Path("double")
   @GET
   String cookieDouble(@CookieParam("c") @Separator("#") double[] array);
}

