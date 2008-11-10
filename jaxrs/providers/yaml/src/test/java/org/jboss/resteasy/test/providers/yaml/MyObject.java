package org.jboss.resteasy.test.providers.yaml;

import java.util.Date;

public class MyObject
{

   private String someText;

   private Date date;

   private MyNestedObject nested = new MyNestedObject();

   public MyNestedObject getNested()
   {
      return nested;
   }

   public void setNested(MyNestedObject nested)
   {
      this.nested = nested;
   }

   public String getSomeText()
   {
      return someText;
   }

   public void setSomeText(String someText)
   {
      this.someText = someText;
   }

   public Date getDate()
   {
      return date;
   }

   public void setDate(Date date)
   {
      this.date = date;
   }


   @Override
   public String toString()
   {
      return "MyObject[" + nested + "," + date + "," + nested + "]";
   }

}
