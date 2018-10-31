package org.jboss.resteasy.test.resource.param.resource;

import java.util.Comparator;

public class MultiValuedParamDefaultParamConverterFromStringClass implements Comparable<MultiValuedParamDefaultParamConverterFromStringClass> {
   private String s;

   public String getS() {
      return s;
   }

   public void setS(String s) {
      this.s = s;
   }

   @Override
   public int compareTo(MultiValuedParamDefaultParamConverterFromStringClass o) {
      return s.compareTo(o.getS());
   }

   public String toString() {
      return "s" + s;
   }

   public static Comp COMP = new Comp();
   static class Comp implements Comparator<MultiValuedParamDefaultParamConverterFromStringClass> {

      @Override
      public int compare(MultiValuedParamDefaultParamConverterFromStringClass o1, MultiValuedParamDefaultParamConverterFromStringClass o2) {
         return o1.getS().compareTo(o2.getS());
      }
   }

   public static MultiValuedParamDefaultParamConverterFromStringClass fromString(String s) {
      MultiValuedParamDefaultParamConverterFromStringClass fs = new MultiValuedParamDefaultParamConverterFromStringClass();
      fs.setS("f" + s);
      return fs;
   }
}
