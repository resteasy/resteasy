package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ejb.Local;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.test.cdi.util.Constants;

@Local
@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public interface EJBBookReader extends MessageBodyReader<EJBBook> {
    int getUses();

    void reset();
}
