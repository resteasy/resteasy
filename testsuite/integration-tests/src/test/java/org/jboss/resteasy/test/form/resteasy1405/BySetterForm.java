package org.jboss.resteasy.test.form.resteasy1405;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

public class BySetterForm
{
   private String name;

   private InputPart data;

   public String getName()
   {
      return name;
   }

   @FormParam("name")
   public void setName(String name)
   {
      this.name = name;
   }

   public InputPart getData()
   {
      return data;
   }

   @FormParam("data")
   public void setData(InputPart data)
   {
      this.data = data;
   }

}