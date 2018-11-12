package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Separator;

@Path("query")
public interface MultiValuedParamDefaultParamConverterQueryResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String queryConstructorSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set")
   @GET
   public String queryConstructorSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   public String queryConstructorSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/separator/array")
   @GET
   public String queryConstructorSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);
 
   @Path("constructor/regex/list")
   @GET
   public String queryConstructorRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String queryConstructorRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set); 
   
   @Path("constructor/regex/sortedset")
   @GET
   public String queryConstructorRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/array")
   @GET
   public String queryConstructorRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   @Path("constructor/default/list")
   @GET
   public String queryConstructorDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String queryConstructorDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String queryConstructorDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/array")
   @GET
   public String queryConstructorDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String queryValueOfSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String queryValueOfSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String queryValueOfSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/array")
   @GET
   public String queryValueOfSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/regex/list")
   @GET
   public String queryValueOfRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String queryValueOfRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String queryValueOfRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/array")
   @GET
   public String queryValueOfRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/default/list")
   @GET
   public String queryValueOfDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set")
   @GET
   public String queryValueOfDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String queryValueOfDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/array")
   @GET
   public String queryValueOfDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("fromString/separator/list")
   @GET
   public String queryFromStringSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String queryFromStringSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String queryFromStringSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/array")
   @GET
   public String queryFromStringSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/regex/list")
   @GET
   public String queryFromStringRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String queryFromStringRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String queryFromStringRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/array")
   @GET
   public String queryFromStringRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/default/list")
   @GET
   public String queryFromStringDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String queryFromStringDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String queryFromStringDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/array")
   @GET
   public String queryFromStringDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("paramConverter/separator/list")
   @GET
   public String queryParamConverterSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String queryParamConverterSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String queryParamConverterSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/array")
   @GET
   public String queryParamConverterSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/regex/list")
   @GET
   public String queryParamConverterRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String queryParamConverterRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String queryParamConverterRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/array")
   @GET
   public String queryParamConverterRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/default/list")
   @GET
   public String queryParamConverterDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String queryParamConverterDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String queryParamConverterDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/array")
   @GET
   public String queryParamConverterDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("boolean")
   @GET
   public String queryBoolean(@HeaderParam("h") @Separator("#") boolean[] array);
   
   @Path("byte")
   @GET
   public String queryByte(@HeaderParam("h") @Separator("#") byte[] array);
   
   @Path("char")
   @GET
   public String queryChar(@HeaderParam("h") @Separator("#") char[] array);
   
   @Path("short")
   @GET
   public String queryShort(@HeaderParam("h") @Separator("#") short[] array);
   
   @Path("int")
   @GET
   public String queryInt(@HeaderParam("h") @Separator("#") int[] array);
   
   @Path("long")
   @GET
   public String queryLong(@HeaderParam("h") @Separator("#") long[] array);
   
   @Path("float")
   @GET
   public String queryFloat(@HeaderParam("h") @Separator("#") float[] array);
 
   @Path("double")
   @GET
   public String queryDouble(@HeaderParam("h") @Separator("#") double[] array);
}
