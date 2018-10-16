package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

@Path("genericsInheritance")
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML})
public class AnnotationInheritanceGenericsImpl extends AnnotationInheritanceGenericsAbstract<AnnotationInheritanceGenericsEntity, Long> {

   public static final Long METHOD_ID_INTERFACE_GET_COLLECTION = 1L;
   public static final Long METHOD_ID_INTERFACE_GET_SINGLE = 2L;
   public static final Long METHOD_ID_INTERFACE_POST = 3L;
   public static final Long METHOD_ID_INTERFACE_PUT = 4L;
   public static final Long METHOD_ID_ABSTRACT_PUT = 5L;

   @Override
   public Collection<AnnotationInheritanceGenericsEntity> get() {
      final AnnotationInheritanceGenericsEntity entity = new AnnotationInheritanceGenericsEntity();
      entity.setId(METHOD_ID_INTERFACE_GET_COLLECTION);

      return Collections.singleton(entity);
   }

   @Override
   public AnnotationInheritanceGenericsEntity get(Long id) {
      final AnnotationInheritanceGenericsEntity entity = new AnnotationInheritanceGenericsEntity();
      entity.setId(METHOD_ID_INTERFACE_GET_SINGLE);

      return entity;
   }

   @Override
   public AnnotationInheritanceGenericsEntity post(AnnotationInheritanceGenericsEntity entity) {
      entity.setId(METHOD_ID_INTERFACE_POST);
      return entity;
   }

   @Override
   public AnnotationInheritanceGenericsEntity put(Long id, AnnotationInheritanceGenericsEntity entity) {
      entity.setId(METHOD_ID_INTERFACE_PUT);
      return entity;
   }

   @Override
   public AnnotationInheritanceGenericsEntity put(AnnotationInheritanceGenericsEntity entity) {
      entity.setId(METHOD_ID_ABSTRACT_PUT);
      return entity;
   }

}
