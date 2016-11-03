package org.jboss.resteasy.test.resource.basic.resource;

public class GenericResourceStudent
{
   private String name;

   public GenericResourceStudent()
   {
   }

   public GenericResourceStudent(String name)
   {
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

   @Override
   public String toString()
   {
      return "Student: " + name;
   }
}
