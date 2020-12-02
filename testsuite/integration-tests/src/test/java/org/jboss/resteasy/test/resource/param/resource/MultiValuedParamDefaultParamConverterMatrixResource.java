package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.Separator;

@Path("matrix")
public class MultiValuedParamDefaultParamConverterMatrixResource {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass

   @Path("constructor/separator/list")
   @GET
   public String matrixConstructorSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/set")
   @GET
   public String matrixConstructorSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String matrixConstructorSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/separator/array")
   @GET
   public String matrixConstructorSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/list")
   @GET
   public String matrixConstructorRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/set")
   @GET
   public String matrixConstructorRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String matrixConstructorRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/regex/array")
   @GET
   public String matrixConstructorRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterConstructorClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/list")
   @GET
   public String matrixConstructorDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterConstructorClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/set")
   @GET
   public String matrixConstructorDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterConstructorClass> set) {
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
   public String matrixConstructorDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterConstructorClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("constructor/default/array")
   @GET
   public String matrixConstructorDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterConstructorClass[] array) {
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
   public String matrixValueOfSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/set")
   @GET
   public String matrixValueOfSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String matrixValueOfSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/separator/array")
   @GET
   public String matrixValueOfSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/list")
   @GET
   public String matrixValueOfRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/set")
   @GET
   public String matrixValueOfRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String matrixValueOfRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/regex/array")
   @GET
   public String matrixValueOfRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/list")
   @GET
   public String matrixValueOfDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterValueOfClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/set")
   @GET
   public String matrixValueOfRegexSetDefault(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterValueOfClass> set) {
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
   public String matrixValueOfDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("valueOf/default/array")
   @GET
   public String matrixValueOfDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterValueOfClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterValueOfClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/list")
   @GET
   public String matrixFromStringSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/set")
   @GET
   public String matrixFromStringSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String matrixFromStringSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/separator/array")
   @GET
   public String matrixFromStringSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/list")
   @GET
   public String matrixFromStringRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/set")
   @GET
   public String matrixFromStringRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String matrixFromStringRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/regex/array")
   @GET
   public String matrixFromStringRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/list")
   @GET
   public String matrixFromStringDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterFromStringClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/set")
   @GET
   public String matrixFromStringDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterFromStringClass> set) {
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
   public String matrixFromStringDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("fromString/default/array")
   @GET
   public String matrixFromStringDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterFromStringClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterFromStringClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/list")
   @GET
   public String matrixParamConverterSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/set")
   @GET
   public String matrixParamConverterSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String matrixParamConverterSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/separator/array")
   @GET
   public String matrixParamConverterSeparatorArray(@MatrixParam("m") @Separator("-") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/list")
   @GET
   public String matrixParamConverterRegexList(@MatrixParam("m") @Separator("[-,;]") List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/set")
   @GET
   public String matrixParamConverterRegexSet(@MatrixParam("m") @Separator("[-,;]") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String matrixParamConverterRegexSortedSet(@MatrixParam("m") @Separator("[-,;]") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/regex/array")
   @GET
   public String matrixParamConverterRegexArray(@MatrixParam("m") @Separator("[-,;]") MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/list")
   @GET
   public String matrixParamConverterDefaultList(@MatrixParam("m") @Separator List<MultiValuedParamDefaultParamConverterParamConverterClass> list) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : list) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/set")
   @GET
   public String matrixParamConverterDefaultSet(@MatrixParam("m") @Separator Set<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
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
   public String matrixParamConverterDefaultSortedSet(@MatrixParam("m") @Separator SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : set) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("paramConverter/default/array")
   @GET
   public String matrixParamConverterDefaultArray(@MatrixParam("m") @Separator MultiValuedParamDefaultParamConverterParamConverterClass[] array) {
      StringBuffer sb = new StringBuffer();
      for (MultiValuedParamDefaultParamConverterParamConverterClass t : array) {
         sb.append(t.getS()).append("|");
      }
      return sb.toString();
   }

   @Path("boolean")
   @GET
   public String matrixBoolean(@HeaderParam("h") @Separator("#") boolean[] array) {
      StringBuffer sb = new StringBuffer();
      for (boolean b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("byte")
   @GET
   public String matrixByte(@HeaderParam("h") @Separator("#") byte[] array) {
      StringBuffer sb = new StringBuffer();
      for (byte b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("char")
   @GET
   public String matrixChar(@HeaderParam("h") @Separator("#") char[] array) {
      StringBuffer sb = new StringBuffer();
      for (char c : array) {
         sb.append(c).append("|");
      }
      return sb.toString();
   }

   @Path("short")
   @GET
   public String matrixShort(@HeaderParam("h") @Separator("#") short[] array) {
      StringBuffer sb = new StringBuffer();
      for (short b : array) {
         sb.append(b).append("|");
      }
      return sb.toString();
   }

   @Path("int")
   @GET
   public String matrixInt(@HeaderParam("h") @Separator("#") int[] array) {
      StringBuffer sb = new StringBuffer();
      for (int i : array) {
         sb.append(i).append("|");
      }
      return sb.toString();
   }

   @Path("long")
   @GET
   public String matrixLong(@HeaderParam("h") @Separator("#") long[] array) {
      StringBuffer sb = new StringBuffer();
      for (long l : array) {
         sb.append(l).append("|");
      }
      return sb.toString();
   }

   @Path("float")
   @GET
   public String matrixFloat(@HeaderParam("h") @Separator("#") float[] array) {
      StringBuffer sb = new StringBuffer();
      for (float f : array) {
         sb.append(f).append("|");
      }
      return sb.toString();
   }

   @Path("double")
   @GET
   public String matrixDouble(@HeaderParam("h") @Separator("#") double[] array) {
      StringBuffer sb = new StringBuffer();
      for (double d : array) {
         sb.append(d).append("|");
      }
      return sb.toString();
   }
}

