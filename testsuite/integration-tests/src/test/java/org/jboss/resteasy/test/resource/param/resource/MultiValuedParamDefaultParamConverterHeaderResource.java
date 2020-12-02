package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("header")
public class MultiValuedParamDefaultParamConverterHeaderResource {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   public String headerConstructorSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/set")
   @GET
   public String headerConstructorSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String headerConstructorSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/array")
   @GET
   public String headerConstructorSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/list")
   @GET
   public String headerConstructorRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/set")
   @GET
   public String headerConstructorRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String headerConstructorRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/array")
   @GET
   public String headerConstructorRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/list")
   @GET
   public String headerConstructorDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/set")
   @GET
   public String headerConstructorDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String headerConstructorDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/array")
   @GET
   public String headerConstructorDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array) {
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
   public String headerValueOfSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/set")
   @GET
   public String headerValueOfSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String headerValueOfSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/array")
   @GET
   public String headerValueOfSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/list")
   @GET
   public String headerValueOfRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/set")
   @GET
   public String headerValueOfRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String headerValueOfRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/array")
   @GET
   public String headerValueOfRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/list")
   @GET
   public String headerValueOfDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/set")
   @GET
   public String headerValueOfRegexSetDefault(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String headerValueOfDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/array")
   @GET
   public String headerValueOfDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/list")
   @GET
   public String headerFromStringSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/set")
   @GET
   public String headerFromStringSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String headerFromStringSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/array")
   @GET
   public String headerFromStringSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/list")
   @GET
   public String headerFromStringRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/set")
   @GET
   public String headerFromStringRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String headerFromStringRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/array")
   @GET
   public String headerFromStringRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/list")
   @GET
   public String headerFromStringDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/set")
   @GET
   public String headerFromStringDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String headerFromStringDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/array")
   @GET
   public String headerFromStringDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/separator/list")
   @GET
   public String headerHeaderDelegateSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/separator/set")
   @GET
   public String headerHeaderDelegateSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set) {
      List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list = new ArrayList<MultiValuedParamDefaultParamConverterHeaderDelegateClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterHeaderDelegateClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/separator/sortedset")
   @GET
   public String headerHeaderDelegateSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/separator/array")
   @GET
   public String headerHeaderDelegateSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/regex/list")
   @GET
   public String headerHeaderDelegateRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/regex/set")
   @GET
   public String headerHeaderDelegateRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set) {
      List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list = new ArrayList<MultiValuedParamDefaultParamConverterHeaderDelegateClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterHeaderDelegateClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/regex/sortedset")
   @GET
   public String headerHeaderDelegateRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/regex/array")
   @GET
   public String headerHeaderDelegateRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/default/list")
   @GET
   public String headerHeaderDelegateDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/default/set")
   @GET
   public String headerHeaderDelegateDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set) {
      List<MultiValuedParamDefaultParamConverterHeaderDelegateClass> list = new ArrayList<MultiValuedParamDefaultParamConverterHeaderDelegateClass>(set);
      Collections.sort(list, MultiValuedParamDefaultParamConverterHeaderDelegateClass.COMP);
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/default/sortedset")
   @GET
   public String headerHeaderDelegateDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterHeaderDelegateClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("headerDelegate/default/array")
   @GET
   public String headerHeaderDelegateDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterHeaderDelegateClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterHeaderDelegateClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/list")
   @GET
   public String headerParamConverterSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/set")
   @GET
   public String headerParamConverterSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String headerParamConverterSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/array")
   @GET
   public String headerParamConverterSeparatorArray(@HeaderParam("h") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/list")
   @GET
   public String headerParamConverterRegexList(@HeaderParam("h") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/set")
   @GET
   public String headerParamConverterRegexSet(@HeaderParam("h") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String headerParamConverterRegexSortedSet(@HeaderParam("h") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/array")
   @GET
   public String headerParamConverterRegexArray(@HeaderParam("h") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/list")
   @GET
   public String headerParamConverterDefaultList(@HeaderParam("h") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/set")
   @GET
   public String headerParamConverterDefaultSet(@HeaderParam("h") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String headerParamConverterDefaultSortedSet(@HeaderParam("h") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/array")
   @GET
   public String headerParamConverterDefaultArray(@HeaderParam("h") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("boolean")
   @GET
   public String headerBoolean(@HeaderParam("h") @Separator("#") boolean[] array) {
      StringBuffer sb = new StringBuffer();
      for (boolean b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("byte")
   @GET
   public String headerByte(@HeaderParam("h") @Separator("#") byte[] array) {
      StringBuffer sb = new StringBuffer();
      for (byte b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("char")
   @GET
   public String headerChar(@HeaderParam("h") @Separator("#") char[] array) {
      StringBuffer sb = new StringBuffer();
      for (char c : array) {
         sb.append(c).append("|");
      }
      return sb.toString();
   }

   @Path("short")
   @GET
   public String headerShort(@HeaderParam("h") @Separator("#") short[] array) {
      StringBuffer sb = new StringBuffer();
      for (short b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("int")
   @GET
   public String headerInt(@HeaderParam("h") @Separator("#") int[] array) {
      StringBuffer sb = new StringBuffer();
      for (int i : array) {
         sb.append(i).append("|");
      }
      return sb.toString();
   }

   @Path("long")
   @GET
   public String headerLong(@HeaderParam("h") @Separator("#") long[] array) {
      StringBuffer sb = new StringBuffer();
      for (long l : array) {
         sb.append(l).append("|");
      }
      return sb.toString();
   }

   @Path("float")
   @GET
   public String headerFloat(@HeaderParam("h") @Separator("#") float[] array) {
      StringBuffer sb = new StringBuffer();
      for (float f : array) {
         sb.append(f).append("|");
      }
      return sb.toString();
   }

   @Path("double")
   @GET
   public String headerDouble(@HeaderParam("h") @Separator("#") double[] array) {
      StringBuffer sb = new StringBuffer();
      for (double d : array) {
         sb.append(d).append("|");
      }
      return sb.toString();
   }
}

