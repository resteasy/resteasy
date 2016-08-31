package org.jboss.resteasy.test.form.resteasy1405;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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