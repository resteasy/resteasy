package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;

/**
 * Created by rsearls.
 */
@JsonbPropertyOrder({"color", "sort", "name", "domesticated"})
public class Cat {

   public static final String CUSTOM_TO_STRING_FORMAT = "custom toString format";

   public static final Integer DEFAULT_TRANSIENT_VAR_VALUE = -1;

   private String name;
   private String sort;
   private String color;
   private boolean domesticated;

   /**
    * This variable should be processed by Jackson2, but this variable should be ignored by JSON-B.
    */
   @JsonbTransient
   private int transientVar;

   // json-b needs the default constructor
   public Cat() {
      super();
      transientVar = DEFAULT_TRANSIENT_VAR_VALUE;
   }

   public Cat(String name, String sort, String color, boolean domesticated, int transientVar) {
      this.name = name;
      this.sort = sort;
      this.color = color;
      this.domesticated = domesticated;
      this.transientVar = transientVar;
   }

   public String getName() {
      return name;
   }

   public Cat setName(String name) {
      this.name = name;
      return this;
   }

   public String getSort() {
      return sort;
   }

   public Cat setSort(String sort) {
      this.sort = sort;
      return this;
   }

   public String getColor() {
      return color;
   }

   public Cat setColor(String color) {
      this.color = color;
      return this;
   }

   public boolean isDomesticated() {
      return domesticated;
   }

   public Cat setDomesticated(boolean domesticated) {
      this.domesticated = domesticated;
      return this;
   }

   public int getTransientVar() {
      return transientVar;
   }

   public void setTransientVar(int transientVar) {
      this.transientVar = transientVar;
   }

   @Override
   public String toString() {
      return String.format("Cat - %s {name='%s', sort='%s', color='%s', domesticated=%s, transientVar=%s}",
              CUSTOM_TO_STRING_FORMAT, name, sort, color, domesticated, transientVar);
   }
}

