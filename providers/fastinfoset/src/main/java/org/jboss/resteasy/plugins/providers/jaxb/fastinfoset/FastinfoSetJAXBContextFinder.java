package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import java.lang.annotation.Annotation;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.jboss.resteasy.annotations.providers.jaxb.JAXBConfig;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextWrapper;
import org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes({ "application/fastinfoset", "application/*+fastinfoset" })
@Produces({ "application/fastinfoset", "application/*+fastinfoset" })
public class FastinfoSetJAXBContextFinder extends XmlJAXBContextFinder implements ContextResolver<JAXBContextFinder> {
    @Override
    protected JAXBContext createContextObject(Annotation[] annotations, Class... classes) throws JAXBException {
        JAXBConfig config = FindAnnotation.findAnnotation(annotations, JAXBConfig.class);
        JAXBContext context = new FastinfoSetContext(classes);
        return new JAXBContextWrapper(context, config);
    }

    @Override
    protected JAXBContext createContextObject(Annotation[] annotations, String contextPath) throws JAXBException {
        JAXBConfig config = FindAnnotation.findAnnotation(annotations, JAXBConfig.class);
        JAXBContext context = new FastinfoSetContext(contextPath);
        return new JAXBContextWrapper(context, config);
    }
}
