package org.jboss.resteasy.plugins.providers.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
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
public class JAXBXmlRootElementProvider extends AbstractJAXBProvider<Object> {

    @Override
    protected boolean isReadWritable(Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType) {
        return type.isAnnotationPresent(XmlRootElement.class)
                && (FindAnnotation.findAnnotation(type, annotations, DoNotUseJAXBProvider.class) == null)
                && !IgnoredMediaTypes.ignored(type, annotations, mediaType);
    }

}
