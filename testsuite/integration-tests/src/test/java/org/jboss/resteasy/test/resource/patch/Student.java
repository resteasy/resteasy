package org.jboss.resteasy.test.resource.patch;

public class Student
{
   private Long id;

   private String name;

   private String school;

   public Long getId()
   {
      return id;
   }

   public Student setId(Long id)
   {
      this.id = id;
      return this;
   }

   public String getName()
   {
      return name;
   }

   public Student setName(String name)
   {
      this.name = name;
      return this;
   }

   public String getSchool()
   {
      return school;
   }

   public Student setSchool(String school)
   {
      this.school = school;
      return this;
   }

}

