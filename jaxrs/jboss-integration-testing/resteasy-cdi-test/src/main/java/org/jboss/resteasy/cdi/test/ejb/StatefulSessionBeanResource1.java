package org.jboss.resteasy.cdi.test.ejb;

import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.cdi.test.Cat;
import org.jboss.resteasy.cdi.test.Dog;
import org.jboss.resteasy.cdi.test.SubResource;

@Stateful
public class StatefulSessionBeanResource1 implements StatefulSessionBeanResource1Local
{
   private static int uniqueId = 0;
   private int id = uniqueId++;
   
   @Inject
   private Cat cat;
   @EJB
   private InjectedStatelessEjbLocal statelessEjb;
   private Cat constructorCat;
   private Cat initializerCat;
   @Context
   private UriInfo uriInfo;
   @QueryParam("foo") 
   String fieldQuery;
   private UriInfo setterUriInfo;
   @Inject
   private SubResource subResource;
   
   public StatefulSessionBeanResource1()
   {
   }

   @Inject
   public StatefulSessionBeanResource1(Cat cat)
   {
      constructorCat = cat;
   }
   
   @Inject
   public void init(Cat cat)
   {
      initializerCat = cat;
   }

   public boolean fieldInjection()
   {
      return cat != null;
   }
   
   public boolean ejbFieldInjection()
   {
      return statelessEjb.foo();
   }
   
   public boolean jaxrsFieldInjection()
   {
      return uriInfo != null;
   }
   
   public String jaxrsFieldInjection2()
   {
      return fieldQuery;
   }
   
   public boolean jaxrsSetterInjection()
   {
      return setterUriInfo != null;
   }
   
   public boolean constructorInjection()
   {
      return constructorCat != null;
   }
   
   public boolean initializerInjection()
   {
      return initializerCat != null;
   }
   
   public String jaxrsMethodInjection(String query)
   {
      return query;
   }
   
   public int getId()
   {
      return id;
   }
   
   public Dog testProviders()
   {
      return new Dog();
   }
   
   @Context
   public void setSetterUriInfo(UriInfo setterUriInfo)
   {
      this.setterUriInfo = setterUriInfo;
   }
   
   public SubResource subResource()
   {
      return subResource;
   }

   @Remove
   public void remove()
   {
   }
}
