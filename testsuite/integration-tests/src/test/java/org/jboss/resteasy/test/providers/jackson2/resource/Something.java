package org.jboss.resteasy.test.providers.jackson2.resource;

import com.fasterxml.jackson.annotation.JsonView;

public class Something
{

   @JsonView(TestJsonView.class)
   private String annotatedValue;

   @JsonView({TestJsonView.class, TestJsonView2.class})
   private String annotatedValue2;

   public Something()
   {
   }

   public Something(String annotatedValue, String annotatedValue2, String notAnnotatedValue)
   {
      this.annotatedValue = annotatedValue;
      this.annotatedValue2 = annotatedValue2;
      this.notAnnotatedValue = notAnnotatedValue;
   }

   private String notAnnotatedValue;

   public String getAnnotatedValue()
   {
      return annotatedValue;
   }

   public void setAnnotatedValue(String annotatedValue)
   {
      this.annotatedValue = annotatedValue;
   }

   public String getNotAnnotatedValue()
   {
      return notAnnotatedValue;
   }

   public void setNotAnnotatedValue(String notAnnotatedValue)
   {
      this.notAnnotatedValue = notAnnotatedValue;
   }

   public String getAnnotatedValue2()
   {
      return annotatedValue2;
   }

   public void setAnnotatedValue2(String annotatedValue2)
   {
      this.annotatedValue2 = annotatedValue2;
   }

}