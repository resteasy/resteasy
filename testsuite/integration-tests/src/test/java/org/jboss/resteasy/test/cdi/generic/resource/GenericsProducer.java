package org.jboss.resteasy.test.cdi.generic.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;


@ApplicationScoped
@SuppressWarnings("unused")
public class GenericsProducer {

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
}
