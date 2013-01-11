package org.jboss.resteasy.cdi.generic;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 14, 2012
 * 
 * @see https://community.jboss.org/message/784561#784561
 */
@ApplicationScoped
@SuppressWarnings("unused")
public class GenericsProducer
{
   @Inject private Logger log;
   
   @Produces
   @HolderBinding
   private ObjectHolder<Object> oh = new ObjectHolder<Object>(Object.class);
    
   @Produces
   @HolderBinding
   private HierarchyHolder<Primate> hh = new HierarchyHolder<Primate>(Primate.class);
   
   @Produces
   @HolderBinding
   private NestedHierarchyHolder<HierarchyHolder<Primate>> nhh = new NestedHierarchyHolder<HierarchyHolder<Primate>>(Primate.class);

   @Produces
   @HolderBinding
   private UpperBoundHierarchyHolder<HierarchyHolder<Primate>> ubhh = new UpperBoundHierarchyHolder<HierarchyHolder<Primate>>(Primate.class);

   @Produces
   @HolderBinding
   private LowerBoundHierarchyHolder<HierarchyHolder<Primate>> lbhh = new LowerBoundHierarchyHolder<HierarchyHolder<Primate>>(Primate.class);
   
   @Produces
   @ResourceBinding
   private UpperBoundTypedResource<HierarchyHolder<Primate>> ubhhr = new UpperBoundTypedResource<HierarchyHolder<Primate>>(Primate.class);

   @Produces
   @ResourceBinding
   private LowerBoundTypedResource<HierarchyHolder<Primate>> lbhhr = new LowerBoundTypedResource<HierarchyHolder<Primate>>(Primate.class);
   
//   @Produces
//   @RequestScoped
////   @ResourceBinding
//   private UpperBoundTypedResource<HierarchyHolder<Primate>> upperBoundProducer()
//   {
//      UpperBoundTypedResource<HierarchyHolder<Primate>> ubhhr = new UpperBoundTypedResource<HierarchyHolder<Primate>>(Primate.class);
//      System.out.println("GenericsProducer.upperBoundProducer() created: " + ubhhr);
//      return ubhhr;
//   }
//
//   @Produces
////   @ResourceBinding
//   private LowerBoundTypedResource<HierarchyHolder<Primate>> lowerBoundProducer()
//   {
//      LowerBoundTypedResource<HierarchyHolder<Primate>> lbhhr = new LowerBoundTypedResource<HierarchyHolder<Primate>>(Primate.class);
//      System.out.println("GenericsProducer.lowerBoundProducer() created: " + lbhhr);
//      return lbhhr;
//   }
}
