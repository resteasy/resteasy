package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Separator;

@Path("query")
public class MultiValuedParamDefaultParamConverterQueryResource {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   public String queryConstructorSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/set")
   @GET
   public String queryConstructorSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      List<MultiValuedParamDefaultParamConverterConstructorClass> list = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterConstructorClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/sortedset")
   @GET
   public String queryConstructorSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/array")
   @GET
   public String queryConstructorSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/list")
   @GET
   public String queryConstructorRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/set")
   @GET
   public String queryConstructorRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      List<MultiValuedParamDefaultParamConverterConstructorClass> list = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterConstructorClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/sortedset")
   @GET
   public String queryConstructorRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/array")
   @GET
   public String queryConstructorRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/list")
   @GET
   public String queryConstructorDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/set")
   @GET
   public String queryConstructorDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      List<MultiValuedParamDefaultParamConverterConstructorClass> list = new ArrayList<MultiValuedParamDefaultParamConverterConstructorClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterConstructorClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/sortedset")
   @GET
   public String queryConstructorDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/array")
   @GET
   public String queryConstructorDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass

   @Path("valueOf/separator/list")
   @GET
   public String queryValueOfSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/set")
   @GET
   public String queryValueOfSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      List<MultiValuedParamDefaultParamConverterValueOfClass> list = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterValueOfClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/sortedset")
   @GET
   public String queryValueOfSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/array")
   @GET
   public String queryValueOfSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/list")
   @GET
   public String queryValueOfRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/set")
   @GET
   public String queryValueOfRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      List<MultiValuedParamDefaultParamConverterValueOfClass> list = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterValueOfClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/sortedset")
   @GET
   public String queryValueOfRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/array")
   @GET
   public String queryValueOfRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/list")
   @GET
   public String queryValueOfDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/set")
   @GET
   public String queryValueOfRegexSetDefault(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      List<MultiValuedParamDefaultParamConverterValueOfClass> list = new ArrayList<MultiValuedParamDefaultParamConverterValueOfClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterValueOfClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/sortedset")
   @GET
   public String queryValueOfDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/array")
   @GET
   public String queryValueOfDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/list")
   @GET
   public String queryFromStringSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/set")
   @GET
   public String queryFromStringSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      List<MultiValuedParamDefaultParamConverterFromStringClass> list = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterFromStringClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/sortedset")
   @GET
   public String queryFromStringSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/array")
   @GET
   public String queryFromStringSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/list")
   @GET
   public String queryFromStringRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/set")
   @GET
   public String queryFromStringRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      List<MultiValuedParamDefaultParamConverterFromStringClass> list = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterFromStringClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/sortedset")
   @GET
   public String queryFromStringRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/array")
   @GET
   public String queryFromStringRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/list")
   @GET
   public String queryFromStringDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/set")
   @GET
   public String queryFromStringDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      List<MultiValuedParamDefaultParamConverterFromStringClass> list = new ArrayList<MultiValuedParamDefaultParamConverterFromStringClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterFromStringClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/sortedset")
   @GET
   public String queryFromStringDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/array")
   @GET
   public String queryFromStringDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/list")
   @GET
   public String queryParamConverterSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/set")
   @GET
   public String queryParamConverterSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      List<MultiValuedParamDefaultParamConverterParamConverterClass> list = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterParamConverterClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/sortedset")
   @GET
   public String queryParamConverterSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/array")
   @GET
   public String queryParamConverterSeparatorArray(@QueryParam("q") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/list")
   @GET
   public String queryParamConverterRegexList(@QueryParam("q") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/set")
   @GET
   public String queryParamConverterRegexSet(@QueryParam("q") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      List<MultiValuedParamDefaultParamConverterParamConverterClass> list = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterParamConverterClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/sortedset")
   @GET
   public String queryParamConverterRegexSortedSet(@QueryParam("q") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/array")
   @GET
   public String queryParamConverterRegexArray(@QueryParam("q") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/list")
   @GET
   public String queryParamConverterDefaultList(@QueryParam("q") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/set")
   @GET
   public String queryParamConverterDefaultSet(@QueryParam("q") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      List<MultiValuedParamDefaultParamConverterParamConverterClass> list = new ArrayList<MultiValuedParamDefaultParamConverterParamConverterClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterParamConverterClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/sortedset")
   @GET
   public String queryParamConverterDefaultSortedSet(@QueryParam("q") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/array")
   @GET
   public String queryParamConverterDefaultArray(@QueryParam("q") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("boolean")
   @GET
   public String queryBoolean(@HeaderParam("h") @Separator("#") boolean[] array) {
      StringBuffer sb = new StringBuffer();
      for (boolean b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("byte")
   @GET
   public String queryByte(@HeaderParam("h") @Separator("#") byte[] array) {
      StringBuffer sb = new StringBuffer();
      for (byte b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("char")
   @GET
   public String queryChar(@HeaderParam("h") @Separator("#") char[] array) {
      StringBuffer sb = new StringBuffer();
      for (char c : array) {
         sb.append(c).append("|");
      }
      return sb.toString();
   }

   @Path("short")
   @GET
   public String queryShort(@HeaderParam("h") @Separator("#") short[] array) {
      StringBuffer sb = new StringBuffer();
      for (short b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("int")
   @GET
   public String queryInt(@HeaderParam("h") @Separator("#") int[] array) {
      StringBuffer sb = new StringBuffer();
      for (int i : array) {
         sb.append(i).append("|");
      }
      return sb.toString();
   }

   @Path("long")
   @GET
   public String queryLong(@HeaderParam("h") @Separator("#") long[] array) {
      StringBuffer sb = new StringBuffer();
      for (long l : array) {
         sb.append(l).append("|");
      }
      return sb.toString();
   }

   @Path("float")
   @GET
   public String queryFloat(@HeaderParam("h") @Separator("#") float[] array) {
      StringBuffer sb = new StringBuffer();
      for (float f : array) {
         sb.append(f).append("|");
      }
      return sb.toString();
   }

   @Path("double")
   @GET
   public String queryDouble(@HeaderParam("h") @Separator("#") double[] array) {
      StringBuffer sb = new StringBuffer();
      for (double d : array) {
         sb.append(d).append("|");
      }
      return sb.toString();
   }
}
