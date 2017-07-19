package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import org.jboss.resteasy.annotations.providers.jaxb.JAXBConfig;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextWrapper;
import org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes({"application/fastinfoset", "application/*+fastinfoset"})
@Produces({"application/fastinfoset", "application/*+fastinfoset"})
public class FastinfoSetJAXBContextFinder extends XmlJAXBContextFinder implements ContextResolver<JAXBContextFinder>
{
   @Override
   protected JAXBContext createContextObject(Annotation[] annotations, Class... classes) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(annotations, JAXBConfig.class);
      JAXBContext context = new FastinfoSetContext(classes);
      return new JAXBContextWrapper(context, config);
   }

   @Override
   protected JAXBContext createContextObject(Annotation[] annotations, String contextPath) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(annotations, JAXBConfig.class);
      JAXBContext context = new FastinfoSetContext(contextPath);
      return new JAXBContextWrapper(context, config);
   }
}
