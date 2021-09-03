package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.annotations.Separator;

@Path("path")
public class MultiValuedParamDefaultParamConverterPathResource {

   //////////////////////////////////
   // MultiValuedParamDefaultProviderConstructorClass

   @Path("constructor/separator/list/{p}")
   @GET
   public String pathConstructorSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/set/{p}")
   @GET
   public String pathConstructorSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      List<MultiValuedParamDefaultParamConverterConstructorClass> list = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterConstructorClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/sortedset/{p}")
   @GET
   public String pathConstructorSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/array/{p}")
   @GET
   public String pathConstructorSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/list/{p}")
   @GET
   public String pathConstructorRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/set/{p}")
   @GET
   public String pathConstructorRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      List<MultiValuedParamDefaultParamConverterConstructorClass> list = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterConstructorClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/sortedset/{p}")
   @GET
   public String pathConstructorRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/array/{p}")
   @GET
   public String pathConstructorRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/list/{p}")
   @GET
   public String pathConstructorDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/set/{p}")
   @GET
   public String pathConstructorDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      List<MultiValuedParamDefaultParamConverterConstructorClass> list = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterConstructorClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/sortedset/{p}")
   @GET
   public String pathConstructorDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/array/{p}")
   @GET
   public String pathConstructorDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   //////////////////////////////////
   // MultiValuedParamDefaultProviderValueOfClass

   @Path("valueOf/separator/list/{p}")
   @GET
   public String pathValueOfSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/set/{p}")
   @GET
   public String pathValueOfSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      List<MultiValuedParamDefaultParamConverterValueOfClass> list = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterValueOfClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/sortedset/{p}")
   @GET
   public String pathValueOfSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/array/{p}")
   @GET
   public String pathValueOfSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/list/{p}")
   @GET
   public String pathValueOfRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/set/{p}")
   @GET
   public String pathValueOfRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      List<MultiValuedParamDefaultParamConverterValueOfClass> list = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterValueOfClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/sortedset/{p}")
   @GET
   public String pathValueOfRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/array/{p}")
   @GET
   public String pathValueOfRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/list/{p}")
   @GET
   public String pathValueOfDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/set/{p}")
   @GET
   public String pathValueOfRegexSetDefault(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      List<MultiValuedParamDefaultParamConverterValueOfClass> list = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterValueOfClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/sortedset/{p}")
   @GET
   public String pathValueOfDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/array/{p}")
   @GET
   public String pathValueOfDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/list/{p}")
   @GET
   public String pathFromStringSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/set/{p}")
   @GET
   public String pathFromStringSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      List<MultiValuedParamDefaultParamConverterFromStringClass> list = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterFromStringClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/sortedset/{p}")
   @GET
   public String pathFromStringSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/array/{p}")
   @GET
   public String pathFromStringSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/list/{p}")
   @GET
   public String pathFromStringRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/set/{p}")
   @GET
   public String pathFromStringRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      List<MultiValuedParamDefaultParamConverterFromStringClass> list = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterFromStringClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/sortedset/{p}")
   @GET
   public String pathFromStringRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/array/{p}")
   @GET
   public String pathFromStringRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/list/{p}")
   @GET
   public String pathFromStringDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/set/{p}")
   @GET
   public String pathFromStringDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      List<MultiValuedParamDefaultParamConverterFromStringClass> list = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterFromStringClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/sortedset/{p}")
   @GET
   public String pathFromStringDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/array/{p}")
   @GET
   public String pathFromStringDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/list/{p}")
   @GET
   public String pathParamConverterSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/set/{p}")
   @GET
   public String pathParamConverterSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      List<MultiValuedParamDefaultParamConverterParamConverterClass> list = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterParamConverterClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/sortedset/{p}")
   @GET
   public String pathParamConverterSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }


   @Path("paramConverter/separator/array/{p}")
   @GET
   public String pathParamConverterSeparatorArray(@PathParam("p") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/list/{p}")
   @GET
   public String pathParamConverterRegexList(@PathParam("p") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/set/{p}")
   @GET
   public String pathParamConverterRegexSet(@PathParam("p") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      List<MultiValuedParamDefaultParamConverterParamConverterClass> list = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterParamConverterClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/sortedset/{p}")
   @GET
   public String pathParamConverterRegexSortedSet(@PathParam("p") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/array/{p}")
   @GET
   public String pathParamConverterRegexArray(@PathParam("p") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/list/{p}")
   @GET
   public String pathParamConverterDefaultList(@PathParam("p") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/set/{p}")
   @GET
   public String pathParamConverterDefaultSet(@PathParam("p") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      List<MultiValuedParamDefaultParamConverterParamConverterClass> list = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterParamConverterClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/sortedset/{p}")
   @GET
   public String pathParamConverterDefaultSortedSet(@PathParam("p") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/array/{p}")
   @GET
   public String pathParamConverterDefaultArray(@PathParam("p") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("boolean")
   @GET
   public String pathBoolean(@HeaderParam("h") @Separator("#") boolean[] array) {
      StringBuffer sb = new StringBuffer();
      for (boolean b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("byte")
   @GET
   public String pathByte(@HeaderParam("h") @Separator("#") byte[] array) {
      StringBuffer sb = new StringBuffer();
      for (byte b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("char")
   @GET
   public String pathChar(@HeaderParam("h") @Separator("#") char[] array) {
      StringBuffer sb = new StringBuffer();
      for (char c : array) {
         sb.append(c).append("|");
      }
      return sb.toString();
   }

   @Path("short")
   @GET
   public String pathShort(@HeaderParam("h") @Separator("#") short[] array) {
      StringBuffer sb = new StringBuffer();
      for (short b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("int")
   @GET
   public String pathInt(@HeaderParam("h") @Separator("#") int[] array) {
      StringBuffer sb = new StringBuffer();
      for (int i : array) {
         sb.append(i).append("|");
      }
      return sb.toString();
   }

   @Path("long")
   @GET
   public String pathLong(@HeaderParam("h") @Separator("#") long[] array) {
      StringBuffer sb = new StringBuffer();
      for (long l : array) {
         sb.append(l).append("|");
      }
      return sb.toString();
   }

   @Path("float")
   @GET
   public String pathFloat(@HeaderParam("h") @Separator("#") float[] array) {
      StringBuffer sb = new StringBuffer();
      for (float f : array) {
         sb.append(f).append("|");
      }
      return sb.toString();
   }

   @Path("double")
   @GET
   public String pathDouble(@HeaderParam("h") @Separator("#") double[] array) {
      StringBuffer sb = new StringBuffer();
      for (double d : array) {
         sb.append(d).append("|");
      }
      return sb.toString();
   }
}

