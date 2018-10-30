package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

public class JsonFilterChild extends JsonFilterParent {
   protected PersonType personType;

   public JsonFilterChild() {

   }

   public JsonFilterChild(final PersonType personType, final int id, final String name) {
      super(name, id);
      this.personType = personType;
   }

   public PersonType getPersonType() {
      return personType;
   }

   public void setPersonType(PersonType personType) {
      this.personType = personType;
   }
}
