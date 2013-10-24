package org.jboss.resteasy.test.async.callback;

import java.io.IOException;

public class ExceptionThrowingStringBean extends StringBean {

   public ExceptionThrowingStringBean(String header) {
      super(header);
   }

   @Override
   public String get() {
      throw new RuntimeException(new IOException(super.get()));
   }

}
