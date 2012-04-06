package org.jboss.resteasy.test.providers.yaml;

public class MyNestedObject
{

   private String moreText;

   public String getMoreText()
   {
      return moreText;
   }

   public void setMoreText(String moreText)
   {
      this.moreText = moreText;
   }

   @Override
   public String toString()
   {
      return "MyNestedObject[" + moreText + "]";
   }

}
