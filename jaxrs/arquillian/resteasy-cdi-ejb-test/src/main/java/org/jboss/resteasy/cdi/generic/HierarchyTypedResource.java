package org.jboss.resteasy.cdi.generic;

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
@Path("hierarchy")
@Dependent
public class HierarchyTypedResource<T extends Animal> implements HierarchyTypedResourceIntf<T>
{  
   @Inject
   private Logger log;
   
   @Inject
   @HolderBinding
   HierarchyHolder<T> typeParameterHierarchy;

   @Override
   @GET
   @Path("injection")
   public Response testGenerics()
   {
      log.info("entering HierarchyTypedResource.testGenerics()");
      log.info(typeParameterHierarchy.getTypeArgument().toString());
      
      boolean result = true;
      if (!typeParameterHierarchy.getTypeArgument().equals(Primate.class))
      {
         log.info("typeParameterHierarchy type argument class should be Primate instead of " + typeParameterHierarchy.getTypeArgument());
         result = false;
      } 
      return result ? Response.ok().build() : Response.serverError().build();
   }
   
   @Override
   @GET
   @Path("decorators")
   public Response testDecorators()
   {
      ArrayList<String> expectedList = new ArrayList<String>();
      expectedList.add(VisitList.CONCRETE_DECORATOR_ENTER);
      expectedList.add(VisitList.CONCRETE_DECORATOR_LEAVE);
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
