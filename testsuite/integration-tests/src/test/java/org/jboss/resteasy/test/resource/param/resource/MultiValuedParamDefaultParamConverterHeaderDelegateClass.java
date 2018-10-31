package org.jboss.resteasy.test.resource.param.resource;

import java.util.Comparator;

public class MultiValuedParamDefaultParamConverterHeaderDelegateClass implements Comparable<MultiValuedParamDefaultParamConverterHeaderDelegateClass> {
   private String s;

   public String getS() {
      return s;
   }

   public void setS(String s) {
      this.s = s;
   }

   @Override
   public int compareTo(MultiValuedParamDefaultParamConverterHeaderDelegateClass o) {
      return s.compareTo(o.getS());
   }

   public static MultiValuedParamDefaultParamConverterHeaderDelegateClass valueOf(String s) {
      MultiValuedParamDefaultParamConverterHeaderDelegateClass hd = new MultiValuedParamDefaultParamConverterHeaderDelegateClass();
      hd.setS("h" + s);
      return hd;
    }

   public static Comp COMP = new Comp();
   static class Comp implements Comparator<MultiValuedParamDefaultParamConverterHeaderDelegateClass> {

      @Override
      public int compare(MultiValuedParamDefaultParamConverterHeaderDelegateClass o1, MultiValuedParamDefaultParamConverterHeaderDelegateClass o2) {
         return o1.getS().compareTo(o2.getS());
      }
   }
}
