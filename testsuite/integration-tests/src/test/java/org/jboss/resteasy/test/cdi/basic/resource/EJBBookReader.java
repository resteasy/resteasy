package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.test.cdi.util.Constants;

@Local
@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public interface EJBBookReader extends MessageBodyReader<EJBBook> {
    int getUses();

    void reset();
}
