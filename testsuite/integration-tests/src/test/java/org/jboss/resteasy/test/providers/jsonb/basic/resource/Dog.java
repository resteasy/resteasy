package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import javax.json.bind.annotation.JsonbPropertyOrder;

/**
 * Created by tterem.
 */
@JsonbPropertyOrder({"name", "sort"})
public class Dog {

   private String name;
   private String sort;

   public Dog() {

   }

   public Dog(final String name, final String sort) {
      this.name = name;
      this.sort = sort;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getSort() {
      return sort;
   }

   public void setSort(String sort) {
      this.sort = sort;
   }

   public void setNameAndSort(String name, String sort) {
      this.name = name;
      this.sort = sort;
   }

   @Override
   public String toString() {
      return "Dog{" +
            "name='" + name + '\'' +
            ", sort='" + sort + '\'' +
            '}';
   }
}
