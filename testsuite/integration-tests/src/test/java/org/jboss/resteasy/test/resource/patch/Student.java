package org.jboss.resteasy.test.resource.patch;

public class Student
{
   private Long id;

   private String firstName;

   private String lastName;

   private String school;

   private String gender;

   public Long getId()
   {
      return id;
   }

   public Student setId(Long id)
   {
      this.id = id;
      return this;
   }

   public String getFirstName()
   {
      return firstName;
   }

   public Student setFirstName(String firstName)
   {
      this.firstName = firstName;
      return this;
   }

   public String getLastName()
   {
      return lastName;
   }

   public Student setLastName(String lastName)
   {
      this.lastName = lastName;
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

   public String getGender()
   {
      return gender;
   }

   public Student setGender(String gender)
   {
      this.gender = gender;
      return this;
   }
}

