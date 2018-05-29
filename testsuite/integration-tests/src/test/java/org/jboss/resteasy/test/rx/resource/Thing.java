package org.jboss.resteasy.test.rx.resource;

public class Thing {

   private String name;

   public Thing() {
   }

   public Thing(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String toString() {
      return "Thing[" + name + "]";
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Thing)) {
         return false;
      }
      return name.equals(((Thing) o).name);
   }
}
