package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.annotations.Separator;

@Path("path")
public interface MultiValuedParamDefaultParamConverterPathResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultProviderConstructorClass
   
   @Path("constructor/separator/list/{p}")
   @GET
   public String pathConstructorSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set/{p}")
   @GET
   public String pathConstructorSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/separator/sortedset/{p}")
   @GET
   public String pathConstructorSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/separator/array/{p}")
   @GET
   public String pathConstructorSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array);
 
   @Path("constructor/regex/list/{p}")
   @GET
   public String pathConstructorRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set/{p}")
   @GET
   public String pathConstructorRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset/{p}")
   @GET
   public String pathConstructorRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/array/{p}")
   @GET
   public String pathConstructorRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   @Path("constructor/default/list/{p}")
   @GET
   public String pathConstructorDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set/{p}")
   @GET
   public String pathConstructorDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset/{p}")
   @GET
   public String pathConstructorDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/array/{p}")
   @GET
   public String pathConstructorDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array);
   
   //////////////////////////////////
   // MultiValuedParamDefaultProviderValueOfClass
   
   @Path("valueOf/separator/list/{p}")
   @GET
   public String pathValueOfSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set/{p}")
   @GET
   public String pathValueOfSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset/{p}")
   @GET
   public String pathValueOfSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/array/{p}")
   @GET
   public String pathValueOfSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/regex/list/{p}")
   @GET
   public String pathValueOfRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set/{p}")
   @GET
   public String pathValueOfRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset/{p}")
   @GET
   public String pathValueOfRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/array/{p}")
   @GET
   public String pathValueOfRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("valueOf/default/list/{p}")
   @GET
   public String pathValueOfDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set/{p}")
   @GET
   public String pathValueOfDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset/{p}")
   @GET
   public String pathValueOfDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/array/{p}")
   @GET
   public String pathValueOfDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array);
   
   @Path("fromString/separator/list/{p}")
   @GET
   public String pathFromStringSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set/{p}")
   @GET
   public String pathFromStringSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset/{p}")
   @GET
   public String pathFromStringSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/array/{p}")
   @GET
   public String pathFromStringSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/regex/list/{p}")
   @GET
   public String pathFromStringRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set/{p}")
   @GET
   public String pathFromStringRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset/{p}")
   @GET
   public String pathFromStringRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/array/{p}")
   @GET
   public String pathFromStringRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("fromString/default/list/{p}")
   @GET
   public String pathFromStringDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set/{p}")
   @GET
   public String pathFromStringDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset/{p}")
   @GET
   public String pathFromStringDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/array/{p}")
   @GET
   public String pathFromStringDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array);
   
   @Path("paramConverter/separator/list/{p}")
   @GET
   public String pathParamConverterSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set/{p}")
   @GET
   public String pathParamConverterSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset/{p}")
   @GET
   public String pathParamConverterSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/array/{p}")
   @GET
   public String pathParamConverterSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/regex/list/{p}")
   @GET
   public String pathParamConverterRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set/{p}")
   @GET
   public String pathParamConverterRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset/{p}")
   @GET
   public String pathParamConverterRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/array/{p}")
   @GET
   public String pathParamConverterRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("paramConverter/default/list/{p}")
   @GET
   public String pathParamConverterDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set/{p}")
   @GET
   public String pathParamConverterDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset/{p}")
   @GET
   public String pathParamConverterDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/array/{p}")
   @GET
   public String pathParamConverterDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array);
   
   @Path("boolean")
   @GET
   public String pathBoolean(@HeaderParam("h") @Separator("#") boolean[] array);
   
   @Path("byte")
   @GET
   public String pathByte(@HeaderParam("h") @Separator("#") byte[] array);
   
   @Path("char")
   @GET
   public String pathChar(@HeaderParam("h") @Separator("#") char[] array);
   
   @Path("short")
   @GET
   public String pathShort(@HeaderParam("h") @Separator("#") short[] array);
   
   @Path("int")
   @GET
   public String pathInt(@HeaderParam("h") @Separator("#") int[] array);
   
   @Path("long")
   @GET
   public String pathLong(@HeaderParam("h") @Separator("#") long[] array);
   
   @Path("float")
   @GET
   public String pathFloat(@HeaderParam("h") @Separator("#") float[] array);
 
   @Path("double")
   @GET
   public String pathDouble(@HeaderParam("h") @Separator("#") double[] array);
}

