package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.IOException;
import java.io.OutputStream;

public class FinalMethodSuperclass {
   protected final void write(ProviderFinalInheritedMethodStringHandler t, OutputStream entityStream) throws IOException {
      entityStream.write(t.getB().getBytes());
   }
}

