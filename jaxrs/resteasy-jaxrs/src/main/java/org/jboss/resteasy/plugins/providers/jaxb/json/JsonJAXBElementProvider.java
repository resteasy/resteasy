/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;

/**
 * A FastinfoSetJAXBElementProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces("application/json")
@Consumes("application/json")
public class JsonJAXBElementProvider extends JAXBElementProvider
{
   private JettisonJAXBContextFactory jettisonFactory = new JettisonJAXBContextFactory(this);

   @Override
   public JAXBContext findJAXBContext(Class<?> type, Annotation[] annotations, MediaType mediaType)
           throws JAXBException
   {
      return jettisonFactory.findJAXBContext(type, annotations, mediaType);
   }

}