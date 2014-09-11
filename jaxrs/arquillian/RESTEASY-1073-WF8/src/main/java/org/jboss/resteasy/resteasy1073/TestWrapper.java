package org.jboss.resteasy.resteasy1073;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestWrapper
{
   private String name;
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
}