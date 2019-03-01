package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Data object containing CDI related data for Resteasy deployment
 */
public class ResteasyCDIDeployment
{
   private final Map<Class<? extends Annotation>, Set<Annotation>> stereotypes = new HashMap<>();

   public void addStereotype(Class<? extends Annotation> stereotypeClass, Set<Annotation> annotations)
   {
      stereotypes.put(stereotypeClass, annotations);
   }

   public Map<Class<? extends Annotation>, Set<Annotation>> getStereotypes()
   {
      return Collections.unmodifiableMap(stereotypes);
   }
}
