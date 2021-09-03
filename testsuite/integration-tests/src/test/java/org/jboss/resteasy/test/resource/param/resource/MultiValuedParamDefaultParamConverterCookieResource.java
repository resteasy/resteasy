package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("cookie")
public class MultiValuedParamDefaultParamConverterCookieResource {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   public String cookieConstructorSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/set")
   @GET
   public String cookieConstructorSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String cookieConstructorSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/array")
   @GET
   public String cookieConstructorSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/list")
   @GET
   public String cookieConstructorRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/set")
   @GET
   public String cookieConstructorRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String cookieConstructorRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/array")
   @GET
   public String cookieConstructorRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/list")
   @GET
   public String cookieConstructorDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/set")
   @GET
   public String cookieConstructorDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String cookieConstructorDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/array")
   @GET
   public String cookieConstructorDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array) {
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
   public String cookieValueOfSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/set")
   @GET
   public String cookieValueOfSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String cookieValueOfSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/array")
   @GET
   public String cookieValueOfSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/list")
   @GET
   public String cookieValueOfRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/set")
   @GET
   public String cookieValueOfRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String cookieValueOfRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/array")
   @GET
   public String cookieValueOfRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/list")
   @GET
   public String cookieValueOfDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/set")
   @GET
   public String cookieValueOfRegexSetDefault(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String cookieValueOfDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/array")
   @GET
   public String cookieValueOfDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/list")
   @GET
   public String cookieFromStringSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/set")
   @GET
   public String cookieFromStringSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String cookieFromStringSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/array")
   @GET
   public String cookieFromStringSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/list")
   @GET
   public String cookieFromStringRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/set")
   @GET
   public String cookieFromStringRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String cookieFromStringRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/array")
   @GET
   public String cookieFromStringRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/list")
   @GET
   public String cookieFromStringDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/set")
   @GET
   public String cookieFromStringDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String cookieFromStringDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/array")
   @GET
   public String cookieFromStringDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/list")
   @GET
   public String cookieParamConverterSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/set")
   @GET
   public String cookieParamConverterSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String cookieParamConverterSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/array")
   @GET
   public String cookieParamConverterSeparatorArray(@CookieParam("c") @Separator("#") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/list")
   @GET
   public String cookieParamConverterRegexList(@CookieParam("c") @Separator("[#-,]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/set")
   @GET
   public String cookieParamConverterRegexSet(@CookieParam("c") @Separator("[#-,]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String cookieParamConverterRegexSortedSet(@CookieParam("c") @Separator("[#-,]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/array")
   @GET
   public String cookieParamConverterRegexArray(@CookieParam("c") @Separator("[#-,]") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/list")
   @GET
   public String cookieParamConverterDefaultList(@CookieParam("c") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/set")
   @GET
   public String cookieParamConverterDefaultSet(@CookieParam("c") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String cookieParamConverterDefaultSortedSet(@CookieParam("c") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/array")
   @GET
   public String cookieParamConverterDefaultArray(@CookieParam("c") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("boolean")
   @GET
   public String cookieBoolean(@CookieParam("c") @Separator("#") boolean[] array) {
      StringBuffer sb = new StringBuffer();
      for (boolean b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("byte")
   @GET
   public String cookieByte(@CookieParam("c") @Separator("#") byte[] array) {
      StringBuffer sb = new StringBuffer();
      for (byte b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("char")
   @GET
   public String cookieChar(@CookieParam("c") @Separator("#") char[] array) {
      StringBuffer sb = new StringBuffer();
      for (char c : array) {
         sb.append(c).append("|");
      }
      return sb.toString();
   }

   @Path("short")
   @GET
   public String cookieShort(@CookieParam("c") @Separator("#") short[] array) {
      StringBuffer sb = new StringBuffer();
      for (short b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("int")
   @GET
   public String cookieShort(@CookieParam("c") @Separator("#") int[] array) {
      StringBuffer sb = new StringBuffer();
      for (int i : array) {
         sb.append(i).append("|");
      }
      return sb.toString();
   }

   @Path("long")
   @GET
   public String cookieLong(@CookieParam("c") @Separator("#") long[] array) {
      StringBuffer sb = new StringBuffer();
      for (long l : array) {
         sb.append(l).append("|");
      }
      return sb.toString();
   }

   @Path("float")
   @GET
   public String cookieFloat(@CookieParam("c") @Separator("#") float[] array) {
      StringBuffer sb = new StringBuffer();
      for (float f : array) {
         sb.append(f).append("|");
      }
      return sb.toString();
   }

   @Path("double")
   @GET
   public String cookieDouble(@CookieParam("c") @Separator("#") double[] array) {
      StringBuffer sb = new StringBuffer();
      for (double d : array) {
         sb.append(d).append("|");
      }
      return sb.toString();
   }
}

