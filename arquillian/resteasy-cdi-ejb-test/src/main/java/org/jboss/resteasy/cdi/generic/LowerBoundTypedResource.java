package org.jboss.resteasy.cdi.generic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *  
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 14, 2012
 */
@Path("lowerbound")
@Dependent
public class LowerBoundTypedResource<T extends HierarchyHolder<? super Primate>> implements LowerBoundTypedResourceIntf<T>
{  
   @Inject
   private Logger log;

   @Inject
   @HolderBinding
   LowerBoundHierarchyHolder<T> typeParameterLowerBound;

   private Class<?> clazz;
   
   public LowerBoundTypedResource()
   {
      System.out.println("LowerBoundTypedResource<?>(): " + this);
   }
   
   public LowerBoundTypedResource(Class<?> clazz)
   {
      this.clazz = clazz;
      System.out.println("LowerBoundTypedResource(" + clazz + "): " + this);
   }
   
   public Type getTypeArgument()
   {
      return clazz;
   }
   
   @Override
   @GET
   @Path("injection")
   public Response testGenerics()
   {
      log.info("entering LowerBoundTypedResource.testGenerics()");
      log.info(typeParameterLowerBound.getTypeArgument().toString());
      
      boolean result = true;
      if (!typeParameterLowerBound.getTypeArgument().equals(Primate.class))
      {
         log.info("typeParameterLowerBound type argument class should be Primate instead of " + typeParameterLowerBound.getTypeArgument());
         result = false;
      }
      return result ? Response.ok().build() : Response.serverError().build();
   }
   
   @GET
   @Path("decorators/clear")
   public Response clear()
   {
      System.out.println("entering UpperBoundTypedResource.clear()");
      VisitList.clear();
      return Response.ok().build();
   }
   
   @GET
   @Path("decorators/execute")
   public Response execute()
   {
      System.out.println("entering UpperBoundTypedResource.execute()");
      return Response.ok().build();
   }
   
   @Override
   @GET
   @Path("decorators/test")
   public Response testDecorators()
   {
      ArrayList<String> expectedList = new ArrayList<String>();
      expectedList.add(VisitList.LOWER_BOUND_DECORATOR_ENTER);
      expectedList.add(VisitList.LOWER_BOUND_DECORATOR_LEAVE);
      ArrayList<String> visitList = VisitList.getList();
      boolean status = expectedList.size() == visitList.size();
      if (!status)
      {
         log.info("expectedList.size() [" + expectedList.size() + "] != visitList.size() [" + visitList.size() + "]");
      }
      else
      {
         for (int i = 0; i < expectedList.size(); i++)
         {
            if (!expectedList.get(i).equals(visitList.get(i)))
            {
               status = false;
               log.info("visitList.get(" + i + ") incorrect: should be: " + expectedList.get(i) + ", is: " + visitList.get(i));
               break;
            }
         }
      }
      if (!status)
      {
         log.info("\rexpectedList: ");
         for (int i = 0; i < expectedList.size(); i++)
         {
            log.info(i + ": " + expectedList.get(i).toString());
         }
         log.info("\rvisitList:");
         for (int i = 0; i < visitList.size(); i++)
         {
            log.info(i + ": " + visitList.get(i).toString());
         }
      }
      return status == true ? Response.ok().build() : Response.serverError().build();
   }
}
