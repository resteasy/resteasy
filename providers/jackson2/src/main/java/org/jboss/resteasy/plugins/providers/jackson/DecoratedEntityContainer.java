package org.jboss.resteasy.plugins.providers.jackson;

public class DecoratedEntityContainer {
   public DecoratedEntityContainer(final Object entity) {
      this.entity = entity;
   }

   private Object entity;

   public Object getEntity() {
      return entity;
   }

   public void setEntity(Object entity) {
      this.entity = entity;
   }
}
