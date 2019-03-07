package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;

public class CallbackExceptionThrowingStringBean extends CallbackStringBean {

   public CallbackExceptionThrowingStringBean(final String header) {
      super(header);
   }

   @Override
   public String get() {
      throw new RuntimeException(new IOException(super.get()));
   }

}
