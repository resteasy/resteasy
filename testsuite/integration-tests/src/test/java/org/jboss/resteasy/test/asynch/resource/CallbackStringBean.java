package org.jboss.resteasy.test.asynch.resource;

public class CallbackStringBean {
   private String header;

   public String get() {
      return header;
   }

   public void set(String header) {
      this.header = header;
   }

   @Override
   public String toString() {
      return "CallbackStringBean. To get a value, use rather #get() method.";
   }

   public CallbackStringBean(final String header) {
      super();
      this.header = header;
   }
}
