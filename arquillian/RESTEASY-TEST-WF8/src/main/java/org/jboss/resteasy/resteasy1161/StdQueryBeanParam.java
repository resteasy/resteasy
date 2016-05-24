package org.jboss.resteasy.resteasy1161;

import javax.validation.constraints.Size;
import javax.ws.rs.QueryParam;

public class StdQueryBeanParam
{
   @Size(max = 5)
   @QueryParam("limit")
   private String limit;

   String getLimit()
   { 
      return limit;
   }
}
