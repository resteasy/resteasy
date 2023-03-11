package org.jboss.resteasy.plugins.providers.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * A JAXBXmlRootElementProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces({ "application/xml", "application/*+xml", "text/xml", "text/*+xml" })
@Consumes({ "application/xml", "application/*+xml", "text/xml", "text/*+xml" })
public class JAXBXmlSeeAlsoProvider extends AbstractJAXBProvider<Object> {
    @Override
    public JAXBContext findJAXBContext(Class<?> type, Annotation[] annotations, MediaType mediaType, boolean reader)
            throws JAXBException {
        ContextResolver<JAXBContextFinder> resolver = providers.getContextResolver(JAXBContextFinder.class, mediaType);
        JAXBContextFinder finder = resolver.getContext(type);
        if (finder == null) {
            if (reader)
                throw new JAXBUnmarshalException(Messages.MESSAGES.couldNotFindJAXBContextFinder(mediaType));
            else
                throw new JAXBMarshalException(Messages.MESSAGES.couldNotFindJAXBContextFinder(mediaType));
        }

        XmlSeeAlso seeAlso = type.getAnnotation(XmlSeeAlso.class);
        return finder.findCacheContext(mediaType, annotations, seeAlso.value());
    }

    @Override
    protected boolean isReadWritable(Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType) {
        return (type.isAnnotationPresent(XmlSeeAlso.class) && !type.isAnnotationPresent(XmlRootElement.class)
                && !type.isAnnotationPresent(XmlType.class))
                && (FindAnnotation.findAnnotation(type, annotations, DoNotUseJAXBProvider.class) == null)
                && !IgnoredMediaTypes.ignored(type, annotations, mediaType);
    }

}
