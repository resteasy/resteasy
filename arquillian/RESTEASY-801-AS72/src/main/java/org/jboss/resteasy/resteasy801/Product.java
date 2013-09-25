package org.jboss.resteasy.resteasy801;

import org.jboss.resteasy.annotations.providers.Jackson2;

@Jackson2
public class Product
{
   protected String name;

   protected int id;

   public Product()
   {
   }

   public Product(int id, String name)
   {
      this.id = id;
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }
}
