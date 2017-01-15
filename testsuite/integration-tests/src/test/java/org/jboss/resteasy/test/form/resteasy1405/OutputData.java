package org.jboss.resteasy.test.form.resteasy1405;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

public class OutputData
{
   private String name;
   private MediaType contentType;
   private List<String> items;

   public OutputData withName(String name)
   {
      this.name = name;
      return this;
   }

   public OutputData withContentType(MediaType contentType)
   {
      this.contentType = contentType;
      return this;
   }

   public OutputData withItems(List<String> items)
   {
      this.items = items;
      return this;
   }

   @Override
   public String toString()
   {
      return new StringBuilder("OutputData[").append("name='").append(name).append("', contentType='")
            .append(contentType).append("', items={").append(join(items, ',')).append("}]").toString();
   }

   private static StringBuilder join(List<String> items, char separator)
   {
      StringBuilder builder = new StringBuilder();
      if (items != null)
      {
         Iterator<String> iter = items.iterator();
         if (iter.hasNext())
         {
            builder.append(iter.next());

            while (iter.hasNext())
            {
               builder.append(separator).append(iter.next());
            }
         }
      }
      return builder;
   }
}