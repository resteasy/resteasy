package org.jboss.resteasy.test.cdi.generic.resource;

import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("concrete")
@Dependent
public class ConcreteResource implements ConcreteResourceIntf {
    private static Logger log = Logger.getLogger(ConcreteResource.class);

    @Inject
    @HolderBinding
    ObjectHolder<?> wildcardObject;

    @Inject
    @HolderBinding
    ObjectHolder<Object> objectObject;

    @Inject
    @HolderBinding
    HierarchyHolder<?> wildcardHierarchy;


    @Inject
    @HolderBinding
    HierarchyHolder<Primate> primateHierarchy;

    @Inject
    @HolderBinding
    HierarchyHolder<? super Australopithecus> lowerBoundHierarchy;

    @Inject
    @HolderBinding
    HierarchyHolder<? extends Animal> upperBoundHierarchy;

    @Inject
    @HolderBinding
    NestedHierarchyHolder<?> wildcardNested;

    @Inject
    @HolderBinding
    NestedHierarchyHolder<HierarchyHolder<Primate>> primateNested;

    @Inject
    @HolderBinding
    NestedHierarchyHolder<HierarchyHolder<? super Australopithecus>> lowerBoundNested;

    @Inject
    @HolderBinding
    NestedHierarchyHolder<HierarchyHolder<? extends Animal>> upperBoundNested;

    @Inject
    @HolderBinding
    UpperBoundHierarchyHolder<?> wildcardUpperBound;

    @Inject
    @HolderBinding
    UpperBoundHierarchyHolder<HierarchyHolder<Primate>> primateUpperBound;

    @Inject
    @HolderBinding
    UpperBoundHierarchyHolder<? extends HierarchyHolder<? extends Animal>> upperBoundUpperBound;

    @Inject
    @HolderBinding
    UpperBoundHierarchyHolder<? extends HierarchyHolder<? super Australopithecus>> lowerBoundUpperBound;

    @Inject
    @HolderBinding
    LowerBoundHierarchyHolder<?> wildcardLowerBound;

    @Inject
    @HolderBinding
    LowerBoundHierarchyHolder<HierarchyHolder<Primate>> primateLowerBound;

    @Inject
    @HolderBinding
    LowerBoundHierarchyHolder<? extends HierarchyHolder<? extends Animal>> upperBoundLowerBound;

    @Inject
    @HolderBinding
    LowerBoundHierarchyHolder<? extends HierarchyHolder<? super Australopithecus>> lowerBoundLowerBound;

    @Override
    @GET
    @Path("injection")
    public Response testGenerics() {
        log.info("entering ConcreteResource.testGenerics()");
        log.info(wildcardObject.getTypeArgument().toString());
        log.info(objectObject.getTypeArgument().toString());
        log.info(wildcardHierarchy.getTypeArgument().toString());
        log.info(primateHierarchy.getTypeArgument().toString());
        log.info(lowerBoundHierarchy.getTypeArgument().toString());
        log.info(upperBoundHierarchy.getTypeArgument().toString());
        log.info(wildcardNested.getTypeArgument().toString());
        log.info(primateNested.getTypeArgument().toString());
        log.info(lowerBoundNested.getTypeArgument().toString());
        log.info(upperBoundNested.getTypeArgument().toString());
        log.info(wildcardUpperBound.getTypeArgument().toString());
        log.info(primateUpperBound.getTypeArgument().toString());
        log.info(lowerBoundUpperBound.getTypeArgument().toString());
        log.info(upperBoundUpperBound.getTypeArgument().toString());
        log.info(wildcardLowerBound.getTypeArgument().toString());
        log.info(primateLowerBound.getTypeArgument().toString());
        log.info(lowerBoundLowerBound.getTypeArgument().toString());
        log.info(upperBoundLowerBound.getTypeArgument().toString());

        boolean result = true;
        if (!wildcardObject.getTypeArgument().equals(Object.class)) {
            log.info("wildcardObject type argument class should be Object instead of " + wildcardObject.getTypeArgument());
            result = false;
        }
        if (!objectObject.getTypeArgument().equals(Object.class)) {
            log.info("objectObject type argument class should be Object instead of " + objectObject.getTypeArgument());
            result = false;
        }
        if (!wildcardHierarchy.getTypeArgument().equals(Primate.class)) {
            log.info("wildcardHierarchy type argument class should be Primate instead of " + wildcardHierarchy.getTypeArgument());
            result = false;
        }
        if (!primateHierarchy.getTypeArgument().equals(Primate.class)) {
            log.info("primateHierarchy type argument class should be Primate instead of " + primateHierarchy.getTypeArgument());
            result = false;
        }
        if (!lowerBoundHierarchy.getTypeArgument().equals(Primate.class)) {
            log.info("lowerBoundHierarchy type argument class should be Primate instead of " + lowerBoundHierarchy.getTypeArgument());
            result = false;
        }
        if (!upperBoundHierarchy.getTypeArgument().equals(Primate.class)) {
            log.info("upperBoundHierarchy type argument class should be Primate instead of " + upperBoundHierarchy.getTypeArgument());
            result = false;
        }
        if (!wildcardNested.getTypeArgument().equals(Primate.class)) {
            log.info("wildcardNested type argument class should be Primate instead of " + wildcardNested.getTypeArgument());
            result = false;
        }
        if (!primateNested.getTypeArgument().equals(Primate.class)) {
            log.info("primateNested type argument class should be Primate instead of " + primateNested.getTypeArgument());
            result = false;
        }
        if (!lowerBoundNested.getTypeArgument().equals(Primate.class)) {
            log.info("lowerBoundNested type argument class should be Primate instead of " + lowerBoundNested.getTypeArgument());
            result = false;
        }
        if (!upperBoundNested.getTypeArgument().equals(Primate.class)) {
            log.info("upperBoundNested type argument class should be Primate instead of " + upperBoundNested.getTypeArgument());
            result = false;
        }
        if (!wildcardUpperBound.getTypeArgument().equals(Primate.class)) {
            log.info("wildcardUpperBound type argument class should be Primate instead of " + wildcardUpperBound.getTypeArgument());
            result = false;
        }
        if (!primateUpperBound.getTypeArgument().equals(Primate.class)) {
            log.info("primateUpperBound type argument class should be Primate instead of " + primateUpperBound.getTypeArgument());
            result = false;
        }
        if (!lowerBoundUpperBound.getTypeArgument().equals(Primate.class)) {
            log.info("lowerBoundUpperBound type argument class should be Primate instead of " + lowerBoundUpperBound.getTypeArgument());
            result = false;
        }
        if (!upperBoundUpperBound.getTypeArgument().equals(Primate.class)) {
            log.info("upperBoundUpperBound type argument class should be Primate instead of " + upperBoundUpperBound.getTypeArgument());
            result = false;
        }
        if (!wildcardLowerBound.getTypeArgument().equals(Primate.class)) {
            log.info("wildcardLowerBound type argument class should be Primate instead of " + wildcardLowerBound.getTypeArgument());
            result = false;
        }
        if (!primateLowerBound.getTypeArgument().equals(Primate.class)) {
            log.info("primateLowerBound type argument class should be Primate instead of " + primateLowerBound.getTypeArgument());
            result = false;
        }
        if (!lowerBoundLowerBound.getTypeArgument().equals(Primate.class)) {
            log.info("lowerBoundLowerBound type argument class should be Primate instead of " + lowerBoundLowerBound.getTypeArgument());
            result = false;
        }
        if (!upperBoundLowerBound.getTypeArgument().equals(Primate.class)) {
            log.info("upperBoundLowerBound type argument class should be Primate instead of " + upperBoundLowerBound.getTypeArgument());
            result = false;
        }
        return result ? Response.ok().build() : Response.serverError().build();
    }

    @GET
    @Path("decorators/clear")
    public Response clear() {
        log.info("entering ConcreteResource.clear()");
        VisitList.clear();
        return Response.ok().build();
    }

    @GET
    @Path("decorators/execute")
    public Response execute() {
        log.info("entering ConcreteResource.execute()");
        return Response.ok().build();
    }

    @Override
    @GET
    @Path("decorators/test")
    public Response testDecorators() {
        log.info("entering ConcreteResource.testDecorators()");
        ArrayList<String> expectedList = new ArrayList<String>();
        expectedList.add(VisitList.CONCRETE_DECORATOR_ENTER);
        expectedList.add(VisitList.CONCRETE_DECORATOR_LEAVE);
        ArrayList<String> visitList = VisitList.getList();
        boolean status = expectedList.size() == visitList.size();
        if (!status) {
            log.info("expectedList.size() [" + expectedList.size() + "] != visitList.size() [" + visitList.size() + "]");
        } else {
            for (int i = 0; i < expectedList.size(); i++) {
                if (!expectedList.get(i).equals(visitList.get(i))) {
                    status = false;
                    log.info("visitList.get(" + i + ") incorrect: should be: " + expectedList.get(i) + ", is: " + visitList.get(i));
                    break;
                }
            }
        }
        if (!status) {
            log.info("\rexpectedList: ");
            for (int i = 0; i < expectedList.size(); i++) {
                log.info(i + ": " + expectedList.get(i).toString());
            }
            log.info("\rvisitList:");
            for (int i = 0; i < visitList.size(); i++) {
                log.info(i + ": " + visitList.get(i).toString());
            }
        }
        return status == true ? Response.ok().build() : Response.serverError().build();
    }
}
