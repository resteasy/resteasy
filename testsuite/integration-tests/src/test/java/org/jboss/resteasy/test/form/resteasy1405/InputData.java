package org.jboss.resteasy.test.form.resteasy1405;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InputData
{
   private List<String> items;

   @XmlElementWrapper(name = "items")
   @XmlElement(name = "item")
   public List<String> getItems()
   {
      return items;
   }

   public void setItems(List<String> items)
   {
      this.items = items;
   }

}
