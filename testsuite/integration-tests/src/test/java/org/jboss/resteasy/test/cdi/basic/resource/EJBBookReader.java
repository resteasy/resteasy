package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.resteasy.test.cdi.util.Constants;

import javax.ejb.Local;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

@Local
@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public interface EJBBookReader extends MessageBodyReader<EJBBook> {
   int getUses();

   void reset();
}
