package org.jboss.resteasy.test.asynch.resource;

public class AsynchContextualDataProduct
{
   protected String name;

   protected int id;

   public AsynchContextualDataProduct()
   {
   }

   public AsynchContextualDataProduct(final int id, final String name)
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
