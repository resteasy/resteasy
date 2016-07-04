package org.jboss.resteasy.resteasy1161;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class TestSubResource
{
   @Max(3) private int y = 7;
   
   @Path("{id}")
   @GET
   public void get(@Max(7) @PathParam("id") Long id, @Valid @BeanParam StdQueryBeanParam queryParams)
   {
      System.out.println("id: " + id);
      System.out.println("queryParam: " + queryParams.getLimit());
   }
   
   @Path("return/{s}")
   @GET
   @Size(max=3)
   public String getString(@PathParam("s") String s)
   {
      System.out.println("s: " + s);
      return s;
   }
}

/*
public class TestSubResource
{
   @Max(3)
   @PathParam("x")
   private int x;
   
   @Path("{x}/{y}")
   @GET
   public void get(@Max(7) @PathParam("y") int y)
   {
      System.out.println("y: " + y);
   }
   
   @Path("return/{s}")
   @GET
   @Size(max=3)
   public String getString(@PathParam("s") String s)
   {
      System.out.println("s: " + s);
      return s;
   }
}
*/