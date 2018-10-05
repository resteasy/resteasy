package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("cookie")
public interface MultiValuedParamDefaultParamConverterCookieResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String cookieConstructorSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterConstructorClass> list);;
 
   @Path("constructor/separator/set")
   @GET
   public String cookieConstructorSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);;

   @Path("constructor/separator/sortedset")
   @GET
   public String cookieConstructorSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/separator/array")
   @GET
   public String cookieConstructorSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterConstructorClass[] array);;

   @Path("constructor/regex/list")
   @GET
   public String cookieConstructorRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String cookieConstructorRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset")
   @GET
   public String cookieConstructorRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/array")
   @GET
   public String cookieConstructorRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   @Path("constructor/default/list")
   @GET
   public String cookieConstructorDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String cookieConstructorDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String cookieConstructorDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/array")
   @GET
   public String cookieConstructorDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String cookieValueOfSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String cookieValueOfSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String cookieValueOfSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/array")
   @GET
   public String cookieValueOfSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/regex/list")
   @GET
   public String cookieValueOfRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String cookieValueOfRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String cookieValueOfRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/array")
   @GET
   public String cookieValueOfRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/default/list")
   @GET
   public String cookieValueOfDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list); 
   
   @Path("valueOf/default/set")
   @GET
   public String cookieValueOfDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String cookieValueOfDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/array")
   @GET
   public String cookieValueOfDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array); 
  
   @Path("fromString/separator/list")
   @GET
   public String cookieFromStringSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String cookieFromStringSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String cookieFromStringSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/array")
   @GET
   public String cookieFromStringSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/regex/list")
   @GET
   public String cookieFromStringRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String cookieFromStringRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String cookieFromStringRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set); 
   
   @Path("fromString/regex/array")
   @GET
   public String cookieFromStringRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/default/list")
   @GET
   public String cookieFromStringDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String cookieFromStringDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String cookieFromStringDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/array")
   @GET
   public String cookieFromStringDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("paramConverter/separator/list")
   @GET
   public String cookieParamConverterSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String cookieParamConverterSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set); 
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String cookieParamConverterSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/array")
   @GET
   public String cookieParamConverterSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/regex/list")
   @GET
   public String cookieParamConverterRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String cookieParamConverterRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String cookieParamConverterRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/array")
   @GET
   public String cookieParamConverterRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/default/list")
   @GET
   public String cookieParamConverterDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String cookieParamConverterDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String cookieParamConverterDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);

   @Path("paramConverter/default/array")
   @GET
   public String cookieParamConverterDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);
 
   @Path("boolean")
   @GET
   public String cookieBoolean(@CookieParam("c") @Separator("#") boolean[] array);
   
   @Path("byte")
   @GET
   public String cookieByte(@CookieParam("c") @Separator("#") byte[] array);
   
   @Path("char")
   @GET
   public String cookieChar(@CookieParam("c") @Separator("#") char[] array);
   
   @Path("short")
   @GET
   public String cookieShort(@CookieParam("c") @Separator("#") short[] array);
   
   @Path("int")
   @GET
   public String cookieInt(@CookieParam("c") @Separator("#") int[] array);
   
   @Path("long")
   @GET
   public String cookieLong(@CookieParam("c") @Separator("#") long[] array);
   
   @Path("float")
   @GET
   public String cookieFloat(@CookieParam("c") @Separator("#") float[] array);
 
   @Path("double")
   @GET
   public String cookieDouble(@CookieParam("c") @Separator("#") double[] array);
}

