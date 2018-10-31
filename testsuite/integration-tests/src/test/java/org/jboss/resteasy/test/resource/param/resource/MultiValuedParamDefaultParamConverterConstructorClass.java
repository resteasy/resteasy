package org.jboss.resteasy.test.resource.param.resource;

import java.util.Comparator;

public class MultiValuedParamDefaultParamConverterConstructorClass implements Comparable<MultiValuedParamDefaultParamConverterConstructorClass> {

   private String s;

   public MultiValuedParamDefaultParamConverterConstructorClass(final String s) {
      this.s = "c" + s;
   }

   public String getS() {
      return s;
   }

   @Override
   public int compareTo(MultiValuedParamDefaultParamConverterConstructorClass o) {
      return s.compareTo(o.getS());
   }

   public String toString() {
      return "s" + s;
   }

   public static Comp COMP = new Comp();
   static class Comp implements Comparator<MultiValuedParamDefaultParamConverterConstructorClass> {

      @Override
      public int compare(MultiValuedParamDefaultParamConverterConstructorClass o1, MultiValuedParamDefaultParamConverterConstructorClass o2) {
         return o1.getS().compareTo(o2.getS());
      }
   }
}
