package org.jboss.resteasy.test.resource.param.resource;

import java.util.Comparator;

public class MultiValuedParamDefaultParamConverterParamConverterClass implements Comparable<MultiValuedParamDefaultParamConverterParamConverterClass>  {
   private String s;

   public String getS() {
      return s;
   }

   public void setS(String s) {
       this.s = s;
   }

   @Override
   public int compareTo(MultiValuedParamDefaultParamConverterParamConverterClass o) {
      return s.compareTo(o.getS());
   }

   public String toString() {
      return "oops";
   }

   public static Comp COMP = new Comp();
   static class Comp implements Comparator<MultiValuedParamDefaultParamConverterParamConverterClass> {

      @Override
      public int compare(MultiValuedParamDefaultParamConverterParamConverterClass o1, MultiValuedParamDefaultParamConverterParamConverterClass o2) {
         return o1.getS().compareTo(o2.getS());
      }
   }
}
