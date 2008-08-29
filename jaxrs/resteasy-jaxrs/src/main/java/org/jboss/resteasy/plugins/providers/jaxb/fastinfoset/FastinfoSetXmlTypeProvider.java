/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import org.jboss.resteasy.annotations.providers.jaxb.JAXBConfig;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextWrapper;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;

/**
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces("application/fastinfoset")
@Consumes("application/fastinfoset")
public class FastinfoSetXmlTypeProvider extends JAXBXmlTypeProvider
{

   @Override
   protected JAXBContext createDefaultJAXBContext(Class<?> type, Annotation[] annotations) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(type, annotations, JAXBConfig.class);
      JAXBContext context = new FastinfoSetContext(type);
      return new JAXBContextWrapper(context, config);
   }
}
