package org.jboss.resteasy.test.form.resteasy1405;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

public class ByFieldForm
{
   @FormParam("name")
   private String name;

   @FormParam("data")
   private InputPart data;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public InputPart getData()
   {
      return data;
   }

   public void setData(InputPart data)
   {
      this.data = data;
   }

}