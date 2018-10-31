package org.jboss.resteasy.test.resource.param.resource;

import java.util.Comparator;

public class MultiValuedParamDefaultParamConverterValueOfClass implements Comparable<MultiValuedParamDefaultParamConverterValueOfClass> {
   private String s;

   public String getS() {
      return s;
   }

   public void setS(String s) {
      this.s = s;
   }

   @Override
   public int compareTo(MultiValuedParamDefaultParamConverterValueOfClass o) {
      return s.compareTo(o.getS());
   }

   public String toString() {
      return "s" + s;
   }

   public static Comp COMP = new Comp();
   static class Comp implements Comparator<MultiValuedParamDefaultParamConverterValueOfClass> {

      @Override
      public int compare(MultiValuedParamDefaultParamConverterValueOfClass o1, MultiValuedParamDefaultParamConverterValueOfClass o2) {
         return o1.getS().compareTo(o2.getS());
      }
   }

   public static MultiValuedParamDefaultParamConverterValueOfClass valueOf(String s) {
      MultiValuedParamDefaultParamConverterValueOfClass vo = new MultiValuedParamDefaultParamConverterValueOfClass();
      vo.setS("v" + s);
      return vo;
   }
}
